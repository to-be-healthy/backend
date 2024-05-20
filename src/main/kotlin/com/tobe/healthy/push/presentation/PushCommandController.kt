package com.tobe.healthy.push.presentation

import com.tobe.healthy.ApiResultResponse
import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.push.application.PushCommandService
import com.tobe.healthy.push.domain.dto.`in`.CommandRegisterToken
import com.tobe.healthy.push.domain.dto.`in`.CommandSendNotification
import com.tobe.healthy.push.domain.dto.out.CommandRegisterTokenResult
import com.tobe.healthy.push.domain.dto.out.CommandSendNotificationResult
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/push")
class PushCommandController(
    private val pushCommandService: PushCommandService,
) {

    @PostMapping("/register")
    fun registerFcmToken(@RequestBody request: CommandRegisterToken,
                         @AuthenticationPrincipal member: CustomMemberDetails): ApiResultResponse<CommandRegisterTokenResult> {
        return ApiResultResponse(
            message = "토큰을 저장하였습니다.",
            data = pushCommandService.registerFcmToken(request, member.memberId)
        )
    }

    @PostMapping
    fun sendPushAlarm(@RequestBody request: CommandSendNotification): ApiResultResponse<CommandSendNotificationResult> {
        return ApiResultResponse(
            message = "푸시 전송에 성공하였습니다.",
            data = pushCommandService.sendPushAlarm(request)
        )
    }

    @PostMapping("/{memberId}")
    fun sendPushAlarm(@PathVariable memberId: Long,
                      @RequestBody request: CommandSendNotification
    ): ApiResultResponse<CommandSendNotificationResult> {
        return ApiResultResponse(
            message = "푸시 전송에 성공하였습니다.",
            data = pushCommandService.sendPushAlarm(memberId, request)
        )
    }
}
