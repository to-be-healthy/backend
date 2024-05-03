package com.tobe.healthy.schedule.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.schedule.application.TrainerScheduleService;
import com.tobe.healthy.schedule.domain.dto.in.RegisterScheduleCommand;
import com.tobe.healthy.schedule.domain.dto.in.RegisterScheduleRequest;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleSearchCond;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleIdInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule/v1")
@Slf4j
@Valid
@Tag(name = "03-01.수업 API", description = "수업 일정 API")
public class TrainerScheduleController {

    private final TrainerScheduleService trainerScheduleService;

    @Operation(summary = "트레이너가 일정을 등록한다.", description = "트레이너가 일정을 등록한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "일정 등록 성공")
            })
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    @PostMapping
    public ResponseHandler<Boolean> registerSchedule(@RequestBody RegisterScheduleRequest request,
                                                     @AuthenticationPrincipal CustomMemberDetails member) {
        return ResponseHandler.<Boolean>builder()
                .data(trainerScheduleService.registerSchedule(request, member.getMemberId()))
                .message("일정 등록에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "트레이너가 일정을 개별 등록한다.", description = "트레이너가 일정을 개별 등록한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "일정 등록 성공")
            })
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    @PostMapping("/individual")
    public ResponseHandler<Boolean> registerIndividualSchedule(@RequestBody RegisterScheduleCommand request,
                                                               @AuthenticationPrincipal CustomMemberDetails member) {
        return ResponseHandler.<Boolean>builder()
                .data(trainerScheduleService.registerIndividualSchedule(request, member.getMemberId()))
                .message("개별 일정 등록에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "트레이너가 전체 일정을 조회한다.", description = "트레이너가 전체 일정을 조회한다. 특정 일자나 기간으로 조회하고 싶으면 DTO를 활용한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "트레이너가 전체 일정 조회 완료")
            })
    @GetMapping("/all")
    public ResponseHandler<List<ScheduleCommandResult>> findAllSchedule(@ParameterObject ScheduleSearchCond searchCond,
                                                                        @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
        return ResponseHandler.<List<ScheduleCommandResult>>builder()
                .data(trainerScheduleService.findAllSchedule(searchCond, customMemberDetails.getMember()))
                .message("전체 일정을 조회했습니다.")
                .build();
    }

    @Operation(summary = "트레이너가 등록된 일정을 취소한다.", description = "트레이너가 등록한 일정을 취소한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "해당 일정을 취소하였습니다."),
                    @ApiResponse(responseCode = "404", description = "해당 일정이 존재하지 않습니다.")
            })
    @DeleteMapping("/trainer/{scheduleId}")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public ResponseHandler<Boolean> cancelScheduleForTrainer(@Parameter(description = "일정 아이디", example = "1") @PathVariable Long scheduleId,
                                                             @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
        return ResponseHandler.<Boolean>builder()
                .data(trainerScheduleService.cancelTrainerSchedule(scheduleId, customMemberDetails.getMemberId()))
                .message("일정을 취소하였습니다.")
                .build();
    }

    @Operation(summary = "트레이너가 학생 노쇼 처리를 한다.", description = "트레이너가 학생 노쇼 처리를 한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "노쇼 처리가 되었습니다."),
                    @ApiResponse(responseCode = "404", description = "해당 일정이 존재하지 않습니다.")
            })
    @DeleteMapping("/no-show/{scheduleId}")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public ResponseHandler<ScheduleIdInfo> updateReservationStatusToNoShow(@Parameter(description = "일정 아이디", example = "1") @PathVariable Long scheduleId,
                                                                           @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
        return ResponseHandler.<ScheduleIdInfo>builder()
                .data(trainerScheduleService.updateReservationStatusToNoShow(scheduleId, customMemberDetails.getMemberId()))
                .message("노쇼 처리되었습니다.")
                .build();
    }

    /**
     * ===========================================공통 부분 시작===========================================
     */
    @Operation(summary = "트레이너 또는 학생이 수업을 신청한다.", description = "트레이너 또는 학생이 수업을 신청한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "수업 신청 완료"),
                    @ApiResponse(responseCode = "404(1)", description = "해당 수업이 존재하지 않습니다."),
                    @ApiResponse(responseCode = "404(2)", description = "신청 할 수 없는 수업입니다.")
            })
    @PostMapping("/{scheduleId}")
    public ResponseHandler<ScheduleIdInfo> reserveSchedule(@Parameter(description = "수업 일정 아이디") @PathVariable Long scheduleId,
                                                           @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
        return ResponseHandler.<ScheduleIdInfo>builder()
                .data(trainerScheduleService.reserveSchedule(scheduleId, customMemberDetails.getMemberId()))
                .message("일정 신청되었습니다.")
                .build();
    }

    @Operation(summary = "트레이너 또는 학생이 수업을 취소한다.", description = "트레이너 또는 학생이 등록한 수업을 취소한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "해당 수업을 취소하였습니다."),
                    @ApiResponse(responseCode = "404", description = "해당 수업이 존재하지 않습니다.")
            })
    @DeleteMapping("/{scheduleId}")
    public ResponseHandler<ScheduleIdInfo> cancelScheduleForMember(@Parameter(description = "일정 아이디", example = "1") @PathVariable Long scheduleId,
                                                                   @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
        return ResponseHandler.<ScheduleIdInfo>builder()
                .data(trainerScheduleService.cancelMemberSchedule(scheduleId, customMemberDetails.getMemberId()))
                .message("수업을 취소하였습니다.")
                .build();
    }

    /**
     * ===========================================공통 부분 종료===========================================
     */
}
