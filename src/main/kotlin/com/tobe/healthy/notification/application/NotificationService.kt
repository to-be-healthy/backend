package com.tobe.healthy.notification.application

import com.tobe.healthy.common.KotlinCustomPaging
import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.MEMBER_NOT_FOUND
import com.tobe.healthy.lessonhistory.repository.LessonHistoryRepository
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.notification.domain.dto.`in`.CommandSendNotification
import com.tobe.healthy.notification.domain.dto.out.CommandNotificationStatusResult
import com.tobe.healthy.notification.domain.dto.out.CommandSendNotificationResult
import com.tobe.healthy.notification.domain.dto.out.RetrieveNotificationWithRedDotResult
import com.tobe.healthy.notification.domain.dto.out.RetrieveNotificationWithRedDotResult.RetrieveNotificationResult
import com.tobe.healthy.notification.domain.entity.Notification
import com.tobe.healthy.notification.domain.entity.NotificationCategory
import com.tobe.healthy.notification.repository.NotificationRepository
import com.tobe.healthy.push.application.PushCommandService
import com.tobe.healthy.push.domain.dto.`in`.CommandSendPushAlarm
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val memberRepository: MemberRepository,
    private val pushCommandService: PushCommandService,
    private val lessonHistoryRepository: LessonHistoryRepository
) {

    fun sendNotification(
        request: CommandSendNotification,
        senderId: Long
    ): CommandSendNotificationResult {

        val sender = memberRepository.findById(senderId)
            .orElseThrow { throw CustomException(MEMBER_NOT_FOUND) }

        val receivers = memberRepository.findMemberTokenById(request.receiverIds)

        val notifications = mutableListOf<Notification>()

        if (receivers.isEmpty()) {
            throw IllegalArgumentException("수신자의 ID가 존재하지 않습니다.")
        }

        val lessonHistory = lessonHistoryRepository.findByIdOrNull(request.lessonHistoryId)

        receivers.stream().forEach { receiver ->

            if (!receiver.memberToken.isNullOrEmpty()) {
                pushCommandService.sendPushAlarm(
                    CommandSendPushAlarm(
                        request.title,
                        request.content,
                        receiver.memberToken!![0].token,
                    )
                )

                val notification = Notification.create(
                    title = request.title,
                    content = request.content,
                    notificationCategory = request.notificationType.category,
                    notificationType = request.notificationType,
                    sender = sender,
                    receiver = receiver,
                    lessonHistory = lessonHistory
                )
                notifications.add(notification)
            }
        }

        notificationRepository.saveAll(notifications)

        return CommandSendNotificationResult.from(notifications)
    }

    fun findAllNotification(
        notificationCategory: NotificationCategory,
        receiverId: Long,
        pageable: Pageable
    ): KotlinCustomPaging<RetrieveNotificationResult> {

        val notification = notificationRepository.findAllByNotificationType(notificationCategory, receiverId, pageable)

        val redDotStatus = notificationRepository.findAllRedDotStatus(notificationCategory, receiverId)

        val results = RetrieveNotificationWithRedDotResult.from(notification, redDotStatus)

        return KotlinCustomPaging(
            content = results.content,
            pageNumber = notification.pageable.pageNumber,
            pageSize = notification.pageable.pageSize,
            totalPages = notification.totalPages,
            totalElements = notification.totalElements,
            isLast = notification.isLast,
            redDotStatus = results.redDotStatus
        )
    }

    fun findRedDotStatus(memberId: Long): Boolean {
        return notificationRepository.findRedDotStatus(memberId)
    }

    fun updateNotificationStatus(
        notificationId: Long,
        receiverId: Long
    ): CommandNotificationStatusResult {

        val notification = notificationRepository.findByIdAndReceiverId(notificationId, receiverId)
            ?: throw IllegalArgumentException("해당 알림이 존재하지 않습니다.")

        notification.updateNotificationStatus()

        return CommandNotificationStatusResult.from(notification)
    }
}