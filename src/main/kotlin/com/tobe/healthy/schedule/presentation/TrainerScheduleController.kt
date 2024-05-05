package com.tobe.healthy.schedule.presentation

import com.tobe.healthy.ApiResultResponse
import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.schedule.application.TrainerScheduleService
import com.tobe.healthy.schedule.domain.dto.`in`.RegisterScheduleCommand
import com.tobe.healthy.schedule.domain.dto.`in`.RegisterScheduleRequest
import com.tobe.healthy.schedule.domain.dto.`in`.ScheduleSearchCond
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult
import com.tobe.healthy.schedule.domain.dto.out.ScheduleIdInfo
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springdoc.core.annotations.ParameterObject
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule/v1")
@Slf4j
@Tag(name = "03-01.수업 API", description = "수업 일정 API")
class TrainerScheduleController(
    private val trainerScheduleService: TrainerScheduleService
) {
    @Operation(
        summary = "트레이너가 일정을 등록한다.", description = "트레이너가 일정을 등록한다.",
        responses = [
            ApiResponse(responseCode = "200", description = "일정 등록 성공"),
            ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다."),
            ApiResponse(responseCode = "400", description = "이미 등록된 일정이 존재합니다.")
        ]
    )
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    @PostMapping
    fun registerSchedule(@RequestBody request: RegisterScheduleRequest, @AuthenticationPrincipal member: CustomMemberDetails): ApiResultResponse<Boolean> {
        return ApiResultResponse(
            message = "일정 등록에 성공하였습니다.",
            data = trainerScheduleService.registerSchedule(request, member.memberId)
        )
    }

    @Operation(
        summary = "트레이너가 일정을 개별 등록한다.", description = "트레이너가 일정을 개별 등록한다.",
        responses = [
            ApiResponse(responseCode = "200", description = "일정 등록 성공"),
            ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다."),
            ApiResponse(responseCode = "400", description = "이미 등록된 일정이 존재합니다.")
        ]
    )
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    @PostMapping("/individual")
    fun registerIndividualSchedule(@RequestBody request: RegisterScheduleCommand, @AuthenticationPrincipal member: CustomMemberDetails): ApiResultResponse<Boolean> {
        return ApiResultResponse(
            message = "개별 일정 등록에 성공하였습니다.",
            data = trainerScheduleService.registerIndividualSchedule(request, member.memberId)
        )
    }

    @Operation(
        summary = "트레이너가 전체 일정을 조회한다.", description = "트레이너가 전체 일정을 조회한다. 특정 일자나 기간으로 조회하고 싶으면 DTO를 활용한다.",
        responses = [
            ApiResponse(responseCode = "200", description = "트레이너가 전체 일정 조회 완료")
        ]
    )
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    fun findAllSchedule(@ParameterObject searchCond: ScheduleSearchCond, @AuthenticationPrincipal customMemberDetails: CustomMemberDetails): ApiResultResponse<List<ScheduleCommandResult?>> {
        return ApiResultResponse(
            message = "전체 일정을 조회했습니다.",
            data = trainerScheduleService.findAllSchedule(searchCond, customMemberDetails.member)
        )
    }

    @Operation(
        summary = "트레이너가 등록된 일정을 취소한다.", description = "트레이너가 등록한 일정을 취소한다.", responses = [
            ApiResponse(responseCode = "200", description = "해당 일정을 취소하였습니다."),
            ApiResponse(responseCode = "404", description = "해당 일정이 존재하지 않습니다.")
        ]
    )
    @DeleteMapping("/trainer/{scheduleId}")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    fun cancelScheduleForTrainer(@Parameter(description = "일정 아이디", example = "1") @PathVariable scheduleId: Long, @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ): ApiResultResponse<Boolean> {
        return ApiResultResponse(
            message = "일정을 취소하였습니다.",
            data = trainerScheduleService.cancelTrainerSchedule(scheduleId, customMemberDetails.memberId)
        )
    }

    @Operation(summary = "트레이너가 학생 노쇼 처리를 한다.", description = "트레이너가 학생 노쇼 처리를 한다.", responses = [
            ApiResponse(responseCode = "200", description = "노쇼 처리가 되었습니다."),
            ApiResponse(responseCode = "404", description = "해당 일정이 존재하지 않습니다.")
        ]
    )
    @DeleteMapping("/no-show/{scheduleId}")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    fun updateReservationStatusToNoShow(
        @Parameter(description = "일정 아이디", example = "1") @PathVariable scheduleId: Long,
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ): ApiResultResponse<ScheduleIdInfo> {
        return ApiResultResponse(
            message = "노쇼 처리되었습니다.",
            data = trainerScheduleService.updateReservationStatusToNoShow(scheduleId, customMemberDetails.memberId)
        )
    }

    @Operation(summary = "트레이너가 학생 노쇼 처리를 취소한다.", description = "트레이너가 학생 노쇼 처리를 취소한다.", responses = [
        ApiResponse(responseCode = "200", description = "노쇼 처리가 취소되었습니다."),
        ApiResponse(responseCode = "404", description = "해당 일정이 존재하지 않습니다.")
    ])
    @PostMapping("/no-show/{scheduleId}")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    fun revertReservationStatusToNoShow(
        @Parameter(description = "일정 아이디", example = "1") @PathVariable scheduleId: Long,
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ): ApiResultResponse<ScheduleIdInfo> {
        return ApiResultResponse(
            message = "노쇼 처리가 취소되었습니다.",
            data = trainerScheduleService.revertReservationStatusToNoShow(scheduleId, customMemberDetails.memberId)
        )
    }
}
