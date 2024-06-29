package com.tobe.healthy.push.application

import com.google.firebase.messaging.*
import com.tobe.healthy.common.error.CustomException
import com.tobe.healthy.common.error.ErrorCode.MEMBER_NOT_FOUND
import com.tobe.healthy.log
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.push.domain.dto.`in`.CommandRegisterToken
import com.tobe.healthy.push.domain.dto.`in`.CommandSendPushAlarm
import com.tobe.healthy.push.domain.dto.`in`.CommandSendPushAlarmToMember
import com.tobe.healthy.push.domain.dto.out.CommandRegisterTokenResult
import com.tobe.healthy.push.domain.dto.out.CommandSendPushAlarmResult
import com.tobe.healthy.push.domain.entity.MemberToken
import com.tobe.healthy.push.repository.MemberTokenRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class PushCommandService(
    private val memberRepository: MemberRepository,
    private val memberTokenRepository: MemberTokenRepository
) {

    fun registerFcmToken(request: CommandRegisterToken, memberId: Long): CommandRegisterTokenResult {
        val findMember = memberRepository.findByIdOrNull(memberId)
            ?: throw CustomException(MEMBER_NOT_FOUND)

        val findMemberToken = memberTokenRepository.findByMemberId(findMember.id)
            ?: let {
                val memberToken = MemberToken.register(findMember, request.token)
                memberTokenRepository.save(memberToken)
            }

        findMemberToken.changeToken(request.token)

        return CommandRegisterTokenResult(
            name = findMember.name,
            token = request.token
        )
    }

    fun sendPushAlarm(request: CommandSendPushAlarm): CommandSendPushAlarmResult {

        val message = createMessage(request.token, request.title, request.message, request.clickUrl)

        val response = FirebaseMessaging
            .getInstance()
            .sendAsync(message)
            .get()

        log.info("Sent message: ${response}")

        return CommandSendPushAlarmResult.from(
            request.title,
            request.message
        )
    }

    private fun createMessage(token: String, title: String, message: String, clickUrl: String? = null): Message {
        return Message.builder()
            .setToken(token)
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(message)
                .build())
            .setWebpushConfig(WebpushConfig.builder()
                .setNotification(WebpushNotification.builder()
                    .setTitle(title)
                    .setBody(message)
                    .setIcon("https://cdn.to-be-healthy.site/origin/profile/default.png?w=96&h=96") // 웹에서 사용할 아이콘 설정
                    .build())
                .build())
            .putData("click_action", clickUrl ?: "")
            .build()
    }

    fun sendPushAlarm(memberId: Long, request: CommandSendPushAlarmToMember): CommandSendPushAlarmResult {
        val findMemberToken = memberTokenRepository.findByMemberId(memberId)
            ?: throw CustomException(MEMBER_NOT_FOUND)

        val message = createMessage(findMemberToken.token, request.title, request.message)

        val response = FirebaseMessaging
            .getInstance()
            .sendAsync(message)
            .get()

        log.info("Sent message: ${response}")

        return CommandSendPushAlarmResult.from(
            request.title,
            request.message
        )
    }
}
