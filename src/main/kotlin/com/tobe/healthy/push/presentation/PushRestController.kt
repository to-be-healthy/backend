package com.tobe.healthy.push.presentation

import com.tobe.healthy.ApiResultResponse
import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.push.application.FirebaseCloudMessageService
import com.tobe.healthy.push.domain.NotificationRequest
import com.tobe.healthy.push.domain.NotificationResponse
import com.tobe.healthy.push.domain.RegisterTokenResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/push")
class PushRestController(
    private val firebaseCloudMessageService: FirebaseCloudMessageService
) {
    @PostMapping("/register")
    fun registerFcmToken(@RequestParam token: String,
                         @AuthenticationPrincipal member: CustomMemberDetails): ApiResultResponse<RegisterTokenResponse> {
        return ApiResultResponse(
            message = "토큰을 저장하였습니다.",
            data = firebaseCloudMessageService.registerFcmToken(token, member.memberId)
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
    fun sendPushAlarm(@PathVariable memberId: Long, @RequestBody request: NotificationRequest): ApiResultResponse<NotificationResponse> {
        return ApiResultResponse(
            message = "푸시 전송에 성공하였습니다.",
            data = firebaseCloudMessageService.sendPushAlarm(memberId, request)
        )
    }
}
