package com.tobe.healthy.gym.presentation

import com.tobe.healthy.ApiResultResponse
import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.gym.application.GymService
import com.tobe.healthy.gym.domain.dto.out.GymListCommandResult
import com.tobe.healthy.gym.domain.dto.out.RegisterGymResponse
import com.tobe.healthy.gym.domain.dto.out.SelectMyGymResponse
import com.tobe.healthy.gym.domain.dto.out.TrainerCommandResult
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/gyms/v1")
@Tag(name = "04-01.헬스장 API", description = "헬스장 조회 API")
class GymController(
    private val gymService: GymService,
) {
    @Operation(
        summary = "관리자 또는 트레이너가 헬스장을 등록한다.",
        description = "관리자 또는 트레이너가 헬스장을 등록한다.",
        responses = [ApiResponse(responseCode = "200", description = "헬스장을 등록하였습니다.")],
    )
    @PostMapping
    fun registerGym(@Parameter(description = "헬스장 이름") @RequestParam name: String): ApiResultResponse<RegisterGymResponse> {
        return ApiResultResponse(
            data = gymService.registerGym(name),
            message = "헬스장을 등록하였습니다."
        )
    }

    @Operation(
        summary = "모든 헬스장을 조회한다.",
        description = "등록된 모든 헬스장을 조회한다.",
        responses = [ApiResponse(responseCode = "200", description = "모든 헬스장을 조회한다.")],
    )
    @GetMapping
    fun findAllGym(): ApiResultResponse<List<GymListCommandResult>> {
        return ApiResultResponse(
            data = gymService.findAllGym(),
            message = "모든 헬스장을 조회하였습니다."
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
    fun selectMyGym(@Parameter(description = "헬스장 ID") @PathVariable gymId: Long,
                    @Parameter(description = "6자리 난수로 구성된 헬스장 가입 번호") joinCode: Int,
                    @AuthenticationPrincipal member: CustomMemberDetails,
    ): ApiResultResponse<SelectMyGymResponse> {
        return ApiResultResponse(
            data = gymService.selectMyGym(gymId, joinCode, member.memberId),
            message = "내 헬스장으로 등록되었습니다."
        )
    }

    @Operation(
        summary = "학생이 헬스장의 모든 트레이너들을 조회한다.",
        description = "학생이 헬스장의 모든 트레이너들을 조회한다.(새로운 트레이너가 상단에 있도록)",
        responses = [ApiResponse(responseCode = "200", description = "헬스장에 모든 트레이너 조회완료")],
    )
    @GetMapping("/{gymId}/trainers")
    fun findAllTrainersByGym(@Parameter(description = "헬스장 ID") @PathVariable(name = "gymId") gymId: Long): ApiResultResponse<List<TrainerCommandResult?>> {
        return ApiResultResponse(
            data = gymService.findAllTrainersByGym(gymId),
            message = "헬스장의 모든 트레이너들을 조회하였습니다."
        )
    }
}
