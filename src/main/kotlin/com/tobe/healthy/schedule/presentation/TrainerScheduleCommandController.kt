package com.tobe.healthy.schedule.presentation

import com.tobe.healthy.ApiResultResponse
import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.schedule.application.TrainerScheduleCommandService
import com.tobe.healthy.schedule.domain.dto.`in`.CommandRegisterDefaultLessonTime
import com.tobe.healthy.schedule.domain.dto.`in`.CommandRegisterIndividualSchedule
import com.tobe.healthy.schedule.domain.dto.`in`.CommandRegisterSchedule
import com.tobe.healthy.schedule.domain.dto.out.*
import com.tobe.healthy.schedule.domain.entity.ReservationStatus
import com.tobe.healthy.schedule.domain.entity.ReservationStatus.COMPLETED
import com.tobe.healthy.schedule.domain.entity.ReservationStatus.NO_SHOW
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/schedule/v1")
@Tag(name = "03-01.수업 API", description = "수업 일정 API")
class TrainerScheduleCommandController(
    private val trainerScheduleCommandService: TrainerScheduleCommandService
) {

    @Operation(
        summary = "트레이너가 기본 수업 시간을 설정한다.",
        responses = [
            ApiResponse(responseCode = "200", description = "기본 수업 시간 등록 성공"),
            ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다."),
            ApiResponse(responseCode = "400", description = "이미 등록된 일정이 존재합니다.")
    ])
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    @PostMapping("/default-lesson-time")
    fun registerDefaultSchedule(@RequestBody request: CommandRegisterDefaultLessonTime,
                                @AuthenticationPrincipal member: CustomMemberDetails
    ): ApiResultResponse<CommandRegisterDefaultLessonTimeResult> {
        return ApiResultResponse(
            message = "기본 수업 시간이 설정되었습니다.",
            data = trainerScheduleCommandService.registerDefaultLessonTime(request, member.memberId)
        )
    }

    @Operation(
        summary = "트레이너가 일정을 등록한다.",
        responses = [
            ApiResponse(responseCode = "200", description = "일정 등록 성공"),
            ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다."),
            ApiResponse(responseCode = "400", description = "이미 등록된 일정이 존재합니다.")
    ])
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    @PostMapping
    fun registerSchedule(@RequestBody request: CommandRegisterSchedule,
                         @AuthenticationPrincipal member: CustomMemberDetails
    ): ApiResultResponse<CommandRegisterScheduleResult> {
        return ApiResultResponse(
            message = "일정 등록에 성공하였습니다.",
            data = trainerScheduleCommandService.registerSchedule(request, member.memberId)
        )
    }

    @Operation(summary = "트레이너가 일정을 개별 등록한다.", responses = [
            ApiResponse(responseCode = "200", description = "일정 등록 성공"),
            ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다."),
            ApiResponse(responseCode = "400", description = "이미 등록된 일정이 존재합니다.")
    ])
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    @PostMapping("/individual")
    fun registerIndividualSchedule(@RequestBody request: CommandRegisterIndividualSchedule,
                                   @AuthenticationPrincipal member: CustomMemberDetails
    ): ApiResultResponse<Boolean> {
        return ApiResultResponse(
            message = "개별 일정 등록에 성공하였습니다.",
            data = trainerScheduleCommandService.registerIndividualSchedule(request, member.memberId)
        )
    }

    @Operation(
        summary = "트레이너가 특정 스케줄을 DISABLED/AVAILABLE로 변경한다.", responses = [
            ApiResponse(responseCode = "200", description = "해당 스케줄을 변경하였습니다."),
            ApiResponse(responseCode = "404", description = "해당 일정이 존재하지 않습니다.")
        ])
    @PostMapping("/trainer/{status}/{scheduleId}")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    fun changeScheduleForTrainer(@PathVariable status: ReservationStatus,
                                 @PathVariable scheduleId: Long,
                                 @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ): ApiResultResponse<CommandScheduleStatusResult> {
        return ApiResultResponse(
            message = "해당 스케줄을 변경하였습니다.",
            data = trainerScheduleCommandService.updateScheduleStatus(status, scheduleId, customMemberDetails.memberId)
        )
    }

    @Operation(
        summary = "트레이너가 학생을 수업에 등록한다.", responses = [
            ApiResponse(responseCode = "200", description = "수업 등록 성공"),
            ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다."),
            ApiResponse(responseCode = "400", description = "이미 등록된 일정이 존재합니다.")
    ])
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    @PostMapping("/{scheduleId}/{studentId}")
    fun registerScheduleForStudent(@PathVariable scheduleId: Long,
                                   @PathVariable studentId: Long,
                                   @AuthenticationPrincipal member: CustomMemberDetails
    ): ApiResultResponse<CommandRegisterScheduleByStudentResult> {
        return ApiResultResponse(
            message = "학생을 수업에 등록하였습니다.",
            data = trainerScheduleCommandService.registerScheduleForStudent(scheduleId, studentId, member.memberId)
        )
    }

    @Operation(
        summary = "트레이너가 학생이 신청한 수업을 취소한다.", responses = [
            ApiResponse(responseCode = "200", description = "수업이 취소되었습니다."),
            ApiResponse(responseCode = "404", description = "해당 일정이 존재하지 않습니다.")
    ])
    @DeleteMapping("/trainer/{scheduleId}")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    fun cancelScheduleForTrainer(@PathVariable scheduleId: Long,
                                 @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ): ApiResultResponse<Boolean> {
        val scheduleResult = trainerScheduleCommandService.cancelTrainerSchedule(scheduleId, customMemberDetails.memberId)
        return ApiResultResponse(
            message = "${scheduleResult.lessonStartTime.format(DateTimeFormatter.ofPattern("a HH시 mm분"))} 수업이 취소되었습니다.",
            data = true
        )
    }

    @Operation(summary = "트레이너가 학생 노쇼 처리를 한다.", responses = [
        ApiResponse(responseCode = "200", description = "노쇼 처리가 되었습니다."),
        ApiResponse(responseCode = "404", description = "해당 일정이 존재하지 않습니다.")
    ])
    @DeleteMapping("/no-show/{scheduleId}")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    fun updateReservationStatusToNoShow(@PathVariable scheduleId: Long,
                                        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ): ApiResultResponse<ScheduleIdInfo> {
        return ApiResultResponse(
            message = "노쇼 처리되었습니다.",
            data = trainerScheduleCommandService.updateReservationStatusToNoShow(NO_SHOW, scheduleId, customMemberDetails.memberId)
        )
    }

    @Operation(summary = "트레이너가 학생 노쇼 처리를 취소한다.", responses = [
        ApiResponse(responseCode = "200", description = "노쇼 처리가 취소되었습니다."),
        ApiResponse(responseCode = "404", description = "해당 일정이 존재하지 않습니다.")
    ])
    @PostMapping("/no-show/{scheduleId}")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    fun revertReservationStatusToNoShow(@PathVariable scheduleId: Long,
                                        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ): ApiResultResponse<ScheduleIdInfo> {
        return ApiResultResponse(
            message = "노쇼 처리가 취소되었습니다.",
            data = trainerScheduleCommandService.updateReservationStatusToNoShow(COMPLETED, scheduleId, customMemberDetails.memberId)
        )
    }
}