package com.tobe.healthy.notification.presentation

import com.tobe.healthy.ApiResultResponse
import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.notification.application.NotificationService
import com.tobe.healthy.notification.domain.dto.`in`.CommandSendNotification
import com.tobe.healthy.notification.domain.dto.`in`.RetrieveNotification
import com.tobe.healthy.notification.domain.dto.out.CommandSendNotificationResult
import com.tobe.healthy.notification.domain.dto.out.RetrieveNotificationDetailResult
import com.tobe.healthy.notification.domain.dto.out.RetrieveNotificationWithRedDotResult
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
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

    @GetMapping
    fun findAllNotification(
        @ParameterObject request: RetrieveNotification,
        @AuthenticationPrincipal member: CustomMemberDetails,
        @ParameterObject pageable: Pageable
    ) : ApiResultResponse<RetrieveNotificationWithRedDotResult> {
        return ApiResultResponse(
            message = "전체 알림을 조회하였습니다.",
            data = notificationService.findAllNotification(request, member.memberId, pageable)
        )
    }

    @GetMapping("/{notificationId}")
    fun findOneNotification(
        @PathVariable notificationId: Long,
        @AuthenticationPrincipal member: CustomMemberDetails
    ) : ApiResultResponse<RetrieveNotificationDetailResult> {
        return ApiResultResponse(
            message = "알림 상세 조회하였습니다.",
            data = notificationService.findOneNotification(notificationId, member.memberId)
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