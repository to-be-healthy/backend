package com.tobe.healthy.gym.presentation

import com.tobe.healthy.ApiResultResponse
import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.gym.application.GymCommandService
import com.tobe.healthy.gym.domain.dto.out.CommandRegisterGymResult
import com.tobe.healthy.gym.domain.dto.out.CommandSelectMyGymResult
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/gyms/v1")
@Tag(name = "04-01.헬스장 API", description = "헬스장 조회 API")
class GymCommandController(
    private val gymCommandService: GymCommandService
) {
    @Operation(
        summary = "관리자 또는 트레이너가 헬스장을 등록한다.",
        description = "관리자 또는 트레이너가 헬스장을 등록한다.",
        responses = [ApiResponse(responseCode = "200", description = "헬스장을 등록하였습니다.")],
    )
    @PostMapping
    fun registerGym(@RequestParam name: String
    ): ApiResultResponse<CommandRegisterGymResult> {
        return ApiResultResponse(
            data = gymCommandService.registerGym(name),
            message = "헬스장을 등록하였습니다."
        )
    }

    @Operation(
        summary = "학생 또는 트레이너가 내가 다니는 헬스장으로 등록한다.",
        description = "학생 또는 트레이너가 내가 다니는 헬스장으로 등록한다.",
        responses = [
            ApiResponse(responseCode = "404", description = "회원을 찾을 수 없습니다."),
            ApiResponse(responseCode = "404", description = "헬스장을 찾을 수 없습니다."),
            ApiResponse(responseCode = "200", description = "내 헬스장으로 등록하였습니다."),
        ],
    )
    @PostMapping("/{gymId}")
    fun selectMyGym(
        @PathVariable gymId: Long,
        joinCode: String?,
        @AuthenticationPrincipal member: CustomMemberDetails,
    ): ApiResultResponse<CommandSelectMyGymResult> {
        return ApiResultResponse(
            data = gymCommandService.selectMyGym(gymId, joinCode, member.memberId),
            message = "내 헬스장으로 등록되었습니다."
        )
    }
}
