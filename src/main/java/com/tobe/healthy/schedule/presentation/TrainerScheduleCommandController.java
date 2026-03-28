package com.tobe.healthy.schedule.presentation;

import com.tobe.healthy.ApiResult;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.schedule.application.TrainerScheduleCommandService;
import com.tobe.healthy.schedule.domain.dto.in.CommandRegisterDefaultLessonTime;
import com.tobe.healthy.schedule.domain.dto.in.CommandRegisterSchedule;
import com.tobe.healthy.schedule.domain.dto.in.CommandUpdateScheduleStatus;
import com.tobe.healthy.schedule.domain.dto.out.*;
import com.tobe.healthy.schedule.domain.entity.ReservationStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/schedule/v1")
@RequiredArgsConstructor
@Tag(name = "03-01.수업 API", description = "수업 일정 API")
public class TrainerScheduleCommandController {

    private final TrainerScheduleCommandService trainerScheduleCommandService;

    @Operation(
            summary = "트레이너가 기본 수업 시간을 설정한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "기본 수업 시간 등록 성공"),
                    @ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다."),
                    @ApiResponse(responseCode = "400", description = "이미 등록된 일정이 존재합니다.")
            }
    )
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    @PostMapping("/default-lesson-time")
    public ApiResult<CommandRegisterDefaultLessonTimeResult> registerDefaultSchedule(
            @RequestBody @Valid CommandRegisterDefaultLessonTime request,
            @AuthenticationPrincipal CustomMemberDetails member
    ) {
        return new ApiResult<>(
                "기본 수업 시간이 설정되었습니다.",
                trainerScheduleCommandService.registerDefaultLessonTime(request, member.getMemberId())
        );
    }

    @Operation(
            summary = "트레이너가 일정을 등록한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "일정 등록 성공"),
                    @ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다."),
                    @ApiResponse(responseCode = "400", description = "이미 등록된 일정이 존재합니다.")
            }
    )
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    @PostMapping
    public ApiResult<CommandRegisterScheduleResult> registerSchedule(
            @RequestBody @Valid CommandRegisterSchedule request,
            @AuthenticationPrincipal CustomMemberDetails member
    ) {
        return new ApiResult<>(
                "일정 등록에 성공하였습니다.",
                trainerScheduleCommandService.registerSchedule(request, member.getMemberId())
        );
    }

    @Operation(
            summary = "트레이너가 특정 스케줄을 DISABLED/AVAILABLE로 변경한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "해당 스케줄을 변경하였습니다."),
                    @ApiResponse(responseCode = "404", description = "해당 일정이 존재하지 않습니다.")
            }
    )
    @PostMapping("/trainer/{status}")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public ApiResult<List<CommandScheduleStatusResult>> changeScheduleForTrainer(
            @PathVariable ReservationStatus status,
            @RequestBody @Valid CommandUpdateScheduleStatus request,
            @AuthenticationPrincipal CustomMemberDetails customMemberDetails
    ) {
        return new ApiResult<>(
                "해당 스케줄을 " + status + "로 변경하였습니다.",
                trainerScheduleCommandService.updateScheduleStatus(request, status, customMemberDetails.getMemberId())
        );
    }

    @Operation(
            summary = "트레이너가 학생을 수업에 등록한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "수업 등록 성공"),
                    @ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다."),
                    @ApiResponse(responseCode = "400", description = "이미 등록된 일정이 존재합니다.")
            }
    )
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    @PostMapping("/{scheduleId}/{studentId}")
    public ApiResult<CommandRegisterScheduleByStudentResult> registerStudentInTrainerSchedule(
            @PathVariable Long scheduleId,
            @PathVariable Long studentId,
            @AuthenticationPrincipal CustomMemberDetails member
    ) {
        return new ApiResult<>(
                "학생을 수업에 등록하였습니다.",
                trainerScheduleCommandService.registerStudentInTrainerSchedule(
                        scheduleId, studentId, member.getMemberId()
                )
        );
    }

    @Operation(
            summary = "트레이너가 학생이 신청한 수업을 취소한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "수업이 취소되었습니다."),
                    @ApiResponse(responseCode = "404", description = "해당 일정이 존재하지 않습니다.")
            }
    )
    @DeleteMapping("/trainer/{scheduleId}")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public ApiResult<CommandCancelStudentReservationResult> cancelScheduleForTrainer(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal CustomMemberDetails customMemberDetails
    ) {
        CommandCancelStudentReservationResult scheduleResult =
                trainerScheduleCommandService.cancelStudentReservation(scheduleId, customMemberDetails.getMemberId());
        return new ApiResult<>(
                scheduleResult.getLessonStartTime().format(DateTimeFormatter.ofPattern("a HH시 mm분")) + " 수업이 취소되었습니다.",
                scheduleResult
        );
    }

    @Operation(
            summary = "트레이너가 학생 노쇼 처리를 한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "노쇼 처리가 되었습니다."),
                    @ApiResponse(responseCode = "404", description = "해당 일정이 존재하지 않습니다.")
            }
    )
    @DeleteMapping("/no-show/{scheduleId}")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public ApiResult<ScheduleIdInfo> updateReservationStatusToNoShow(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal CustomMemberDetails customMemberDetails
    ) {
        return new ApiResult<>(
                "노쇼 처리되었습니다.",
                trainerScheduleCommandService.updateReservationStatusToNoShow(
                        scheduleId, customMemberDetails.getMemberId()
                )
        );
    }

    @Operation(
            summary = "트레이너가 학생 노쇼 처리를 취소한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "노쇼 처리가 취소되었습니다."),
                    @ApiResponse(responseCode = "404", description = "해당 일정이 존재하지 않습니다.")
            }
    )
    @PostMapping("/no-show/{scheduleId}")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public ApiResult<ScheduleIdInfo> revertReservationStatusToNoShow(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal CustomMemberDetails customMemberDetails
    ) {
        return new ApiResult<>(
                "노쇼 처리가 취소되었습니다.",
                trainerScheduleCommandService.cancelReservationStatusToNoShow(
                        scheduleId, customMemberDetails.getMemberId()
                )
        );
    }
}
