package com.tobe.healthy.push.application

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.WebpushConfig
import com.google.firebase.messaging.WebpushNotification
import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.MEMBER_NOT_FOUND
import com.tobe.healthy.log
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.push.domain.dto.`in`.CommandRegisterToken
import com.tobe.healthy.push.domain.dto.`in`.CommandSendNotification
import com.tobe.healthy.push.domain.dto.out.CommandRegisterTokenResult
import com.tobe.healthy.push.domain.dto.out.CommandSendNotificationResult
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

        memberTokenRepository.findByMemberId(findMember.id)?.changeToken(request.token) ?: let {
            val memberToken = MemberToken.register(findMember, request.token)
            memberTokenRepository.save(memberToken)
        }

        return CommandRegisterTokenResult(
            name = findMember.name,
            token = request.token
        )
    }

    fun sendPushAlarm(request: CommandSendNotification): CommandSendNotificationResult {
        val message = createMessage(request.token, request.title, request.message)

        val response = FirebaseMessaging
            .getInstance()
            .sendAsync(message)
            .get()

        log.info("Sent message: ${response}")

        return CommandSendNotificationResult.from(
            request.title,
            request.message
        )
    }

    private fun createMessage(token: String?, title: String, message: String): Message? =
        Message.builder()
            .setToken(token)
            .setWebpushConfig(
                WebpushConfig.builder()
                    .putHeader("ttl", "300")
                    .setNotification(
                        WebpushNotification(
                            title,
                            message
                        )
                    )
                    .build()
            )
            .build()

    fun sendPushAlarm(memberId: Long, request: CommandSendNotification): CommandSendNotificationResult {
        val findMemberToken = memberTokenRepository.findByMemberId(memberId)
            ?: throw CustomException(MEMBER_NOT_FOUND)

        val message = createMessage(findMemberToken.token, request.title, request.message)

        val response = FirebaseMessaging
            .getInstance()
            .sendAsync(message)
            .get()

        log.info("Sent message: ${response}")

        return CommandSendNotificationResult.from(
            request.title,
            request.message
        )
    }
}
