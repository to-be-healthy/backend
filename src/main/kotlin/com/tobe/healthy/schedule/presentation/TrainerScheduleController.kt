package com.tobe.healthy.schedule.presentation

import com.tobe.healthy.ApiResultResponse
import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.schedule.application.TrainerScheduleService
import com.tobe.healthy.schedule.domain.dto.`in`.TrainerSchedule
import com.tobe.healthy.schedule.domain.dto.`in`.TrainerScheduleByDate
import com.tobe.healthy.schedule.domain.dto.out.TrainerDefaultLessonTimeResult
import com.tobe.healthy.schedule.domain.dto.out.TrainerScheduleByDateResult
import com.tobe.healthy.schedule.domain.dto.out.TrainerScheduleResult
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/schedule/v1")
@Tag(name = "03-01.수업 API", description = "수업 일정 API")
class TrainerScheduleController(
    private val trainerScheduleService: TrainerScheduleService
) {

    @Operation(
        summary = "트레이너가 기본 수업 시간을 조회한다.",
        responses = [
            ApiResponse(responseCode = "200", description = "일정 조회 성공"),
            ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다."),
        ]
    )
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    @GetMapping("/default-lesson-time")
    fun findDefaultSchedule(@AuthenticationPrincipal member: CustomMemberDetails): ApiResultResponse<TrainerDefaultLessonTimeResult> {
        return ApiResultResponse(
            message = "기본 수업 시간 조회에 성공하였습니다.",
            data = trainerScheduleService.findDefaultLessonTime(member.memberId)
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
    fun findAllSchedule(@ParameterObject trainerSchedule: TrainerSchedule,
                        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails): ApiResultResponse<TrainerScheduleResult?> {
        return ApiResultResponse(
            message = "전체 일정을 조회했습니다.",
            data = trainerScheduleService.findAllSchedule(trainerSchedule, customMemberDetails.memberId)
        )
    }

    @Operation(
        summary = "트레이너가 특정 날짜의 일정을 조회한다.",
        responses = [
            ApiResponse(responseCode = "200", description = "트레이너가 특정 날짜의 일정 조회 완료")
        ]
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    fun findOneSchedule(queryTrainerSchedule: TrainerScheduleByDate,
                        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails): ApiResultResponse<TrainerScheduleByDateResult?> {
        return ApiResultResponse(
            message = "특정 날짜의 일정을 조회했습니다.",
            data = trainerScheduleService.findOneTrainerTodaySchedule(queryTrainerSchedule, customMemberDetails.memberId)
        )
    }
}