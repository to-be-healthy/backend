package com.tobe.healthy.notification.presentation

import com.tobe.healthy.ApiResultResponse
import com.tobe.healthy.common.KotlinCustomPaging
import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.notification.application.NotificationService
import com.tobe.healthy.notification.domain.dto.`in`.CommandSendNotification
import com.tobe.healthy.notification.domain.dto.out.CommandNotificationStatusResult
import com.tobe.healthy.notification.domain.dto.out.CommandSendNotificationResult
import com.tobe.healthy.notification.domain.dto.out.RetrieveNotificationWithRedDotResult.RetrieveNotificationResult
import com.tobe.healthy.notification.domain.entity.NotificationCategory
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/notification/v1")
class NotificationController(
    private val notificationService: NotificationService
) {

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    fun sendNotification(
        @RequestBody request: CommandSendNotification,
        @AuthenticationPrincipal member: CustomMemberDetails
    ) : ApiResultResponse<CommandSendNotificationResult> {
        return ApiResultResponse(
            message = "알림 전송에 성공하였습니다.",
            data = notificationService.sendNotification(request, member.memberId)
        )
    }

    @GetMapping("/all/{notificationCategory}")
    fun findAllNotification(
        @PathVariable notificationCategory: NotificationCategory,
        @AuthenticationPrincipal member: CustomMemberDetails,
        @ParameterObject @PageableDefault(size = 10) pageable: Pageable
    ) : ApiResultResponse<KotlinCustomPaging<RetrieveNotificationResult>> {
        return ApiResultResponse(
            message = "전체 알림을 조회하였습니다.",
            data = notificationService.findAllNotification(notificationCategory, member.memberId, pageable)
        )
    }

    @GetMapping("/read/{notificationId}")
    fun updateNotificationStatus(
        @PathVariable notificationId: Long,
        @AuthenticationPrincipal member: CustomMemberDetails,
    ) : ApiResultResponse<CommandNotificationStatusResult> {
        return ApiResultResponse(
            message = "해당 알림을 읽음 처리 하였습니다.",
            data = notificationService.updateNotificationStatus(notificationId, member.memberId)
        )
    }

    @GetMapping("/red-dot")
    fun findNotificationWithRedDot(
        @AuthenticationPrincipal member: CustomMemberDetails
    ) : ApiResultResponse<Boolean> {
        return ApiResultResponse(
            message = "red-dot 상태를 조회하였습니다.",
            data = notificationService.findRedDotStatus(member.memberId)
        )
    }
}