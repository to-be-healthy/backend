package com.tobe.healthy.notification.application

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.MEMBER_NOT_FOUND
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.notification.domain.dto.`in`.CommandSendNotification
import com.tobe.healthy.notification.domain.dto.`in`.RetrieveNotification
import com.tobe.healthy.notification.domain.dto.out.CommandSendNotificationResult
import com.tobe.healthy.notification.domain.dto.out.RetrieveNotificationDetailResult
import com.tobe.healthy.notification.domain.dto.out.RetrieveNotificationWithRedDotResult
import com.tobe.healthy.notification.domain.dto.out.RetrieveNotificationWithRedDotResult.RetrieveNotificationResult
import com.tobe.healthy.notification.domain.entity.Notification
import com.tobe.healthy.notification.repository.NotificationRepository
import com.tobe.healthy.push.application.PushCommandService
import com.tobe.healthy.push.domain.dto.`in`.CommandSendPushAlarm
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val memberRepository: MemberRepository,
    private val pushCommandService: PushCommandService
) {
    fun sendNotification(
        request: CommandSendNotification,
        memberId: Long
    ): CommandSendNotificationResult {

        val sender = memberRepository.findById(memberId)
            .orElseThrow { throw CustomException(MEMBER_NOT_FOUND) }

        val receivers = memberRepository.findMemberTokenById(request.receiverIds)

        val notifications = mutableListOf<Notification>()

        if (receivers.isEmpty()) {
            throw IllegalArgumentException("수신자를 입력해 주세요.")
        }

        receivers.forEach { receiver ->

            pushCommandService.sendPushAlarm(CommandSendPushAlarm(receiver.memberToken?.firstOrNull()?.token, request.title, request.content))

            val notification = Notification.create(
                title = request.title,
                content = request.content,
                notificationType = request.notificationType,
                sender = sender,
                receiver = receiver
            )

            notifications.add(notification)
        }

        notificationRepository.saveAll(notifications)

        return CommandSendNotificationResult.from(notifications)
    }

    fun findAllNotification(
        request: RetrieveNotification,
        receiverId: Long
    ): RetrieveNotificationWithRedDotResult {
        val notification = notificationRepository.findAllByNotificationType(request.notificationType, receiverId)
            .map { RetrieveNotificationResult.from(it) }

        val redDotStatus = notificationRepository.findAllRedDotStatus(request.notificationType, receiverId)

        return RetrieveNotificationWithRedDotResult.from(notification, redDotStatus)
    }

    fun findOneNotification(
        notificationId: Long,
        receiverId: Long
    ): RetrieveNotificationDetailResult {

        val notification = notificationRepository.findOneById(notificationId, receiverId)
            ?: throw IllegalArgumentException("해당 알림이 존재하지 않습니다.")

        notification.readNotification()

        return RetrieveNotificationDetailResult.from(notification)
    }
}