package com.tobe.healthy.schedule.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.schedule.application.ScheduleWaitingService;
import com.tobe.healthy.schedule.domain.dto.out.MyStandbyScheduleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule/waiting/v1")
@Slf4j
@Valid
@Tag(name = "03-02. 수업 대기 API", description = "수업 대기 API")
public class ScheduleWaitingController {

    private final ScheduleWaitingService scheduleWaitingService;

    @Operation(summary = "학생이 수업 대기 신청을 한다.", description = "학생이 신청완료된 수업에 대기 신청을 한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "수업 대기 신청 완료")
            })
    @PostMapping("/{scheduleId}")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseHandler<Boolean> registerStandBySchedule(@Parameter(description = "일정 아이디", example = "1") @PathVariable Long scheduleId,
                                                            @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
        return ResponseHandler.<Boolean>builder()
                .data(scheduleWaitingService.registerStandBySchedule(scheduleId, customMemberDetails.getMemberId()))
                .message("대기 신청 되었습니다.")
                .build();
    }

    @Operation(summary = "학생이 대기 신청을 취소한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "수업 대기 취소 완료")
            })
    @DeleteMapping("/{scheduleId}")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseHandler<Boolean> cancelStandBySchedule(@Parameter(description = "일정 아이디", example = "1") @PathVariable Long scheduleId,
                                                          @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
        return ResponseHandler.<Boolean>builder()
                .data(scheduleWaitingService.cancelStandBySchedule(scheduleId, customMemberDetails.getMemberId()))
                .message("대기 신청이 취소되었습니다.")
                .build();
    }

	@Operation(summary = "학생이 대기중인 예약을 조회한다.", description = "학생이 대기중인 예약을 조회한다.",
			responses = {
					@ApiResponse(responseCode = "200", description = "학생이 대기중인 예약을 조회하였습니다.")
			})
	@GetMapping("/my-standby")
	@PreAuthorize("hasAuthority('ROLE_STUDENT')")
	public ResponseHandler<MyStandbyScheduleResponse> findAllMyStandbySchedule(@AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
		return ResponseHandler.<MyStandbyScheduleResponse>builder()
				.data(scheduleWaitingService.findAllMyStandbySchedule(customMemberDetails.getMemberId()))
				.message("학생이 대기중인 예약을 조회하였습니다.")
				.build();
	}
}
