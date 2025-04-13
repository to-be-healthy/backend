package com.tobe.healthy.push.application

import com.google.firebase.messaging.*
import com.tobe.healthy.common.error.CustomException
import com.tobe.healthy.common.error.ErrorCode.MEMBER_NOT_FOUND
import com.tobe.healthy.log
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.push.domain.dto.`in`.CommandRegisterToken
import com.tobe.healthy.push.domain.dto.`in`.CommandRegisterTokenWithWebView
import com.tobe.healthy.push.domain.dto.`in`.CommandSendPushAlarm
import com.tobe.healthy.push.domain.dto.`in`.CommandSendPushAlarmToMember
import com.tobe.healthy.push.domain.dto.out.CommandRegisterTokenResult
import com.tobe.healthy.push.domain.dto.out.CommandSendPushAlarmResult
import com.tobe.healthy.push.domain.entity.DeviceType.WEB
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

    fun registerFcmToken(
        request: CommandRegisterToken,
        memberId: Long
    ): CommandRegisterTokenResult {
        val findMember = memberRepository.findByIdOrNull(memberId)
            ?: throw CustomException(MEMBER_NOT_FOUND)

        val findMemberToken = memberTokenRepository.findByMemberId(findMember.id)
            ?: let {
                val memberToken = MemberToken.register(findMember, request.token, WEB)
                memberTokenRepository.save(memberToken)
            }

        findMemberToken.changeToken(request.token, WEB)

        return CommandRegisterTokenResult(
            name = findMember.name,
            token = request.token
        )
    }

    fun registerFcmTokenWithWebView(request: CommandRegisterTokenWithWebView) {
        val findMember = memberRepository.findByIdOrNull(request.memberId)
            ?: throw CustomException(MEMBER_NOT_FOUND)

        val findMemberToken = memberTokenRepository.findByMemberId(findMember.id)
            ?: let {
                val memberToken =
                    MemberToken.register(findMember, request.token, request.deviceType)
                memberTokenRepository.save(memberToken)
            }

        findMemberToken.changeToken(request.token, request.deviceType)
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

    private fun createMessage(
        token: String,
        title: String,
        message: String,
        clickUrl: String? = null
    ): Message {
        return Message.builder()
            .setNotification(
                Notification.builder()
                    .setTitle(title)
                    .setBody(message)
                    .setImage("https://cdn.to-be-healthy.shop/origin/profile/default.png?w=96&h=96")
                    .build()
            )
            .setAndroidConfig(
                AndroidConfig.builder().setTtl((3600 * 1000).toLong()).setNotification(
                    AndroidNotification.builder()
                        .setClickAction(clickUrl ?: "")
                        .build()
                ).build()
            )
            .setApnsConfig(
                ApnsConfig.builder().setAps(
                    Aps.builder()
                        .setAlert(
                            ApsAlert.builder()
                                .setTitle(title)
                                .setBody(message)
                                .build()
                        )
                        .setSound("default")
                        .build()
                )
                    .putHeader("apns-push-type", "alert")
                    .putHeader("apns-priority", "10")
                    .putHeader("apns-topic", "site.tobehealthy.webview") // 여기에 실제 번들 ID를 입력
                    .build()
            )
            .setWebpushConfig(
                WebpushConfig.builder()
                    .setNotification(
                        WebpushNotification(
                            title,
                            message,
                        )
                    )
                    .setFcmOptions(WebpushFcmOptions.withLink(clickUrl ?: ""))
                    .build()
            )
            .setToken(token)
            .build()
    }

    fun sendPushAlarm(
        memberId: Long,
        request: CommandSendPushAlarmToMember
    ): CommandSendPushAlarmResult {
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
