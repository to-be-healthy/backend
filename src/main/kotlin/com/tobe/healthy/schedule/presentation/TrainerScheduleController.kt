package com.tobe.healthy.schedule.presentation

import com.tobe.healthy.ApiResultResponse
import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.schedule.application.TrainerScheduleService
import com.tobe.healthy.schedule.domain.dto.out.ScheduleIdInfo
import com.tobe.healthy.schedule.entity.`in`.*
import com.tobe.healthy.schedule.entity.out.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/schedule/v1")
@Tag(name = "03-01.수업 API", description = "수업 일정 API")
class TrainerScheduleController(
    private val trainerScheduleService: TrainerScheduleService
) {

    @Operation(
        summary = "트레이너가 기본 수업 시간을 설정한다.", description = "트레이너가 기본 수업 시간을 설정한다.",
        responses = [
            ApiResponse(responseCode = "200", description = "일정 등록 성공"),
            ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다."),
            ApiResponse(responseCode = "400", description = "이미 등록된 일정이 존재합니다.")
        ]
    )
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    @PostMapping("/default-lesson-time")
    fun registerDefaultSchedule(@RequestBody request: RegisterDefaultLessonTimeRequest,
                                @AuthenticationPrincipal member: CustomMemberDetails): ApiResultResponse<RegisterDefaultLessonTimeResponse> {
        return ApiResultResponse(
            message = "기본 수업 시간이 설정되었습니다.",
            data = trainerScheduleService.registerDefaultLessonTime(request, member.memberId)
        )
    }

    @Operation(
        summary = "트레이너가 기본 수업 시간을 조회한다.", description = "트레이너가 기본 수업 시간을 조회한다.",
        responses = [
            ApiResponse(responseCode = "200", description = "일정 조회 성공"),
            ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다."),
        ]
    )
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    @GetMapping("/default-lesson-time")
    fun findDefaultSchedule(@AuthenticationPrincipal member: CustomMemberDetails): ApiResultResponse<TrainerScheduleResponse> {
        return ApiResultResponse(
            message = "기본 수업 조회에 성공하였습니다.",
            data = trainerScheduleService.findDefaultLessonTime(member.memberId)
        )
    }

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
    fun registerSchedule(@RequestBody request: RegisterScheduleRequest,
                         @AuthenticationPrincipal member: CustomMemberDetails): ApiResultResponse<ScheduleRegisterResponse> {
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
    fun registerIndividualSchedule(@RequestBody request: RegisterScheduleCommand,
                                   @AuthenticationPrincipal member: CustomMemberDetails): ApiResultResponse<Boolean> {
        return ApiResultResponse(
            message = "개별 일정 등록에 성공하였습니다.",
            data = trainerScheduleService.registerIndividualSchedule(request, member.memberId)
        )
    }

    @Operation(
        summary = "트레이너가 학생을 일정에 등록한다.", description = "트레이너가 학생을 일정에 등록한다.",
        responses = [
            ApiResponse(responseCode = "200", description = "일정 등록 성공"),
            ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다."),
            ApiResponse(responseCode = "400", description = "이미 등록된 일정이 존재합니다.")
        ]
    )
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    @PostMapping("/{scheduleId}/{studentId}")
    fun registerScheduleForStudent(@PathVariable scheduleId: Long,
                                   @PathVariable studentId: Long,
                                   @AuthenticationPrincipal member: CustomMemberDetails): ApiResultResponse<RegisterScheduleForStudentResponse> {
        return ApiResultResponse(
            message = "일정에 학생을 등록하였습니다.",
            data = trainerScheduleService.registerScheduleForStudent(scheduleId, studentId, member.memberId)
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
    fun findAllSchedule(@ParameterObject searchCond: ScheduleSearchCond,
                        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails): ApiResultResponse<LessonResponse?> {
        return ApiResultResponse(
            message = "전체 일정을 조회했습니다.",
            data = trainerScheduleService.findAllSchedule(searchCond, customMemberDetails.memberId)
        )
    }

    @Operation(
        summary = "트레이너가 특정 날짜의 일정을 조회한다.", description = "트레이너가 특정 날짜의 일정을 조회한다.",
        responses = [
            ApiResponse(responseCode = "200", description = "트레이너가 특정 날짜의 일정 조회 완료")
        ]
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    fun findOneSchedule(searchCond: TrainerTodayScheduleSearchCond,
                        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails): ApiResultResponse<TrainerTodayScheduleResponse?> {
        return ApiResultResponse(
            message = "특정 날짜의 일정을 조회했습니다.",
            data = trainerScheduleService.findOneTrainerTodaySchedule(searchCond, customMemberDetails.memberId)
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
    fun cancelScheduleForTrainer(@Parameter(description = "일정 아이디", example = "1") @PathVariable scheduleId: Long,
                                 @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ): ApiResultResponse<Boolean> {
        val lessonStartTime = trainerScheduleService.cancelTrainerSchedule(scheduleId, customMemberDetails.memberId)
        return ApiResultResponse(
            message = "${lessonStartTime.format(DateTimeFormatter.ofPattern("a HH시 mm분"))} 수업이 취소되었습니다.",
            data = true
        )
    }

    @Operation(
        summary = "트레이너가 특정 일을 휴무일로 변경한다.", description = "트레이너가 특정 일을 휴무일로 변경한다.", responses = [
            ApiResponse(responseCode = "200", description = "해당 일을 휴무일로 변경하였습니다."),
            ApiResponse(responseCode = "404", description = "해당 일정이 존재하지 않습니다.")
        ]
    )
    @PostMapping("/trainer/change-closed-day")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    fun cancelScheduleForTrainer(@RequestParam lessonDt: String,
                                 @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ): ApiResultResponse<Boolean> {
        return ApiResultResponse(
            message = "휴무일로 변경되었습니다.",
            data = trainerScheduleService.updateLessonDtToClosedDay(lessonDt, customMemberDetails.memberId)
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
