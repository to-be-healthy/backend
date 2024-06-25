package com.tobe.healthy.notification.application

import io.kotest.core.spec.style.StringSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
class NotificationServiceTest(
    private val notificationService: NotificationService
) : StringSpec({

    "매일 오후 10시에 전송하는 배치 알림을 테스트한다" {
        notificationService.sendFeedbackNotificationToTrainer()
    }
})
