package com.tobe.healthy.push.presentation

import com.tobe.healthy.ApiResultResponse
import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.push.application.FirebaseCloudMessageService
import com.tobe.healthy.push.domain.NotificationRequest
import com.tobe.healthy.push.domain.NotificationResponse
import com.tobe.healthy.push.domain.RegisterTokenRequest
import com.tobe.healthy.push.domain.RegisterTokenResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/push")
class PushRestController(
    private val firebaseCloudMessageService: FirebaseCloudMessageService
) {

    @PostMapping("/register")
    fun registerFcmToken(@RequestBody request: RegisterTokenRequest,
                         @AuthenticationPrincipal member: CustomMemberDetails): ApiResultResponse<RegisterTokenResponse> {
        return ApiResultResponse(
            message = "토큰을 저장하였습니다.",
            data = firebaseCloudMessageService.registerFcmToken(request, member.memberId)
        )
    }

    @PostMapping
    fun sendPushAlarm(@RequestBody request: NotificationRequest): ApiResultResponse<NotificationResponse> {
        return ApiResultResponse(
            message = "푸시 전송에 성공하였습니다.",
            data = firebaseCloudMessageService.sendPushAlarm(request)
        )
    }

    @PostMapping("/{memberId}")
    fun sendPushAlarm(@PathVariable memberId: Long,
                      @RequestBody request: NotificationRequest): ApiResultResponse<NotificationResponse> {
        return ApiResultResponse(
            message = "푸시 전송에 성공하였습니다.",
            data = firebaseCloudMessageService.sendPushAlarm(memberId, request)
        )
    }
}
