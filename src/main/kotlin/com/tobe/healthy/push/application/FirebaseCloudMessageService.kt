package com.tobe.healthy.push.application

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.WebpushConfig
import com.google.firebase.messaging.WebpushNotification
import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.MEMBER_NOT_FOUND
import com.tobe.healthy.log
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.push.domain.MemberToken
import com.tobe.healthy.push.domain.NotificationRequest
import com.tobe.healthy.push.domain.NotificationResponse
import com.tobe.healthy.push.domain.RegisterTokenResponse
import com.tobe.healthy.push.repository.MemberTokenRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class FirebaseCloudMessageService(
    private val memberRepository: MemberRepository,
    private val memberTokenRepository: MemberTokenRepository
) {

    fun registerFcmToken(token: String, memberId: Long): RegisterTokenResponse {
        val findMember = memberRepository.findByIdOrNull(memberId)
            ?: throw CustomException(MEMBER_NOT_FOUND)

        memberTokenRepository.findByMemberId(findMember.id)?.let {
            it.changeToken(token)
        } ?: let {
            val memberToken = MemberToken.register(findMember, token)
            memberTokenRepository.save(memberToken)
        }

        return RegisterTokenResponse(
            name = findMember.name,
            token = token
        )
    }

    fun sendPushAlarm(request: NotificationRequest): NotificationResponse {
        val message = Message.builder()
            .setToken(request.token)
            .setWebpushConfig(
                WebpushConfig.builder()
                    .putHeader("ttl", "300")
                    .setNotification(
                        WebpushNotification(
                            request.title,
                            request.message
                        )
                    )
                    .build()
            )
            .build()

        val response = FirebaseMessaging
            .getInstance()
            .sendAsync(message)
            .get()

        log.info("Sent message: ${response}")

        return NotificationResponse.from(
            request.title,
            request.message
        )
    }

    fun sendPushAlarm(memberId: Long, request: NotificationRequest): NotificationResponse {
        val findMemberToken = memberTokenRepository.findByMemberId(memberId) ?: throw CustomException(MEMBER_NOT_FOUND)

        val message = Message.builder()
            .setToken(findMemberToken.token)
            .setWebpushConfig(
                WebpushConfig.builder()
                    .putHeader("ttl", "300")
                    .setNotification(
                        WebpushNotification(
                            request.title,
                            request.message
                        )
                    )
                    .build()
            )
            .build()

        val response = FirebaseMessaging
            .getInstance()
            .sendAsync(message)
            .get()

        log.info("Sent message: ${response}")

        return NotificationResponse.from(
            request.title,
            request.message
        )
    }
}
