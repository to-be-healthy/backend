package com.tobe.healthy.notification.application

import com.tobe.healthy.common.KotlinCustomPaging
import com.tobe.healthy.common.NotificationSenderInfo
import com.tobe.healthy.log
import com.tobe.healthy.member.domain.entity.AlarmStatus.DISABLE
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.notification.domain.dto.`in`.CommandSendNotification
import com.tobe.healthy.notification.domain.dto.out.CommandNotificationStatusResult
import com.tobe.healthy.notification.domain.dto.out.CommandSendNotificationResult
import com.tobe.healthy.notification.domain.dto.out.RetrieveNotificationWithRedDotResult
import com.tobe.healthy.notification.domain.dto.out.RetrieveNotificationWithRedDotResult.RetrieveNotificationResult
import com.tobe.healthy.notification.domain.entity.Notification
import com.tobe.healthy.notification.domain.entity.NotificationCategory
import com.tobe.healthy.notification.domain.entity.NotificationCategory.COMMUNITY
import com.tobe.healthy.notification.domain.entity.NotificationCategory.SCHEDULE
import com.tobe.healthy.notification.domain.entity.NotificationType.FEEDBACK
import com.tobe.healthy.notification.repository.NotificationRepository
import com.tobe.healthy.push.application.PushCommandService
import com.tobe.healthy.push.domain.dto.`in`.CommandSendPushAlarm
import com.tobe.healthy.schedule.repository.TrainerScheduleRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val memberRepository: MemberRepository,
    private val pushCommandService: PushCommandService,
    private val trainerScheduleRepository: TrainerScheduleRepository
) {

    fun sendNotificationFromSystem(
        request: CommandSendNotification
    ): CommandSendNotificationResult {

        val receivers = memberRepository.findMemberTokenById(request.receiverIds)

        if (receivers.isEmpty()) {
            throw IllegalArgumentException("수신자의 ID가 존재하지 않습니다.")
        }

        val notifications = mutableListOf<Notification>()

        receivers.forEach { receiver ->

            if (request.notificationCategory == COMMUNITY && receiver.communityAlarmStatus == DISABLE) {
                throw IllegalArgumentException("커뮤니티 알림을 거부한 수신자입니다.")
            }

            receiver.memberToken.firstOrNull()?.let { memberToken ->
                pushCommandService.sendPushAlarm(CommandSendPushAlarm(request.title, request.content, memberToken.token, request.clickUrl, memberToken.deviceType))

                val notification = Notification.create(
                    title = request.title,
                    content = request.content,
                    notificationCategory = request.notificationCategory,
                    notificationType = request.notificationType,
                    receiver = receiver,
                    targetId = request.targetId,
                    clickUrl = request.clickUrl,
                    studentId = request.studentId,
                    studentName = request.studentName
                )
                log.info("notification: ${notification}")
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
            content = results.content.ifEmpty { null },
            pageNumber = notification.pageable.pageNumber,
            pageSize = notification.pageable.pageSize,
            totalPages = notification.totalPages,
            totalElements = notification.totalElements,
            isLast = notification.isLast,
            redDotStatus = results.redDotStatus,
            sender = NotificationSenderInfo.getSenderInfo()
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

    fun sendFeedbackNotificationToTrainer() {
        trainerScheduleRepository.findAllFeedbackNotificationToTrainer().forEach {
            sendNotificationFromSystem(
                CommandSendNotification(
                    title = FEEDBACK.description,
                    content = FEEDBACK.content.format(it.count),
                    receiverIds = listOf(it.trainerId),
                    notificationType = FEEDBACK,
                    notificationCategory = SCHEDULE,
                    clickUrl = "https://www.to-be-healthy.site/trainer/manage/feedback"
                )
            )
        }
    }
}