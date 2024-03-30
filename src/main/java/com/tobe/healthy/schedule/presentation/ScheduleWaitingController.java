package com.tobe.healthy.schedule.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.schedule.application.ScheduleWaitingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule/waiting/v1")
@Slf4j
@Valid
@Tag(name = "03-02.수업 대기 API", description = "수업 대기 API")
public class ScheduleWaitingController {

	private final ScheduleWaitingService scheduleWaitingService;

	@Operation(summary = "수업 대기 신청을 한다.", responses = {
			@ApiResponse(responseCode = "200", description = "수업 대기 신청 완료")
	})
	@PostMapping("/{scheduleId}")
	public ResponseHandler<Boolean> registerStandBySchedule(@Parameter(description = "일정 아이디") @PathVariable Long scheduleId,
															@AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
		return ResponseHandler.<Boolean>builder()
				.data(scheduleWaitingService.registerStandBySchedule(scheduleId, customMemberDetails.getMemberId()))
				.message("대기 신청 되었습니다.")
				.build();
	}

	@Operation(summary = "신청한 수업의 대기를 취소한다.", responses = {
			@ApiResponse(responseCode = "200", description = "수업 대기 취소 완료")
	})
	@DeleteMapping("/{scheduleId}")
	public ResponseHandler<Boolean> cancelStandBySchedule(@Parameter(description = "일정 아이디") @PathVariable Long scheduleId,
														  @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
		return ResponseHandler.<Boolean>builder()
				.data(scheduleWaitingService.cancelStandBySchedule(scheduleId, customMemberDetails.getMemberId()))
				.message("대기 신청이 취소되었습니다.")
				.build();
	}
}