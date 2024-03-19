package com.tobe.healthy.schedule.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.schedule.application.ScheduleService;
import com.tobe.healthy.schedule.domain.dto.in.AutoCreateScheduleCommand;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleRegisterCommand;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleSearchCond;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedule")
@Slf4j
@Tag(name = "02.Schedule", description = "수업 일정 API")
public class ScheduleController {

	private final ScheduleService scheduleService;

	@Operation(summary = "자동으로 수업 일정을 생성한다.", responses = {
		@ApiResponse(responseCode = "200", description = "자동 수업 일정 생성 완료")
	})
	@PostMapping("/create")
	public ResponseHandler<TreeMap<LocalDate, ArrayList<ScheduleInfo>>> createSchedule(@RequestBody @Valid AutoCreateScheduleCommand request) {
		return ResponseHandler.<TreeMap<LocalDate, ArrayList<ScheduleInfo>>>builder()
			.statusCode(HttpStatus.OK)
			.data(scheduleService.autoCreateSchedule(request))
			.message("일정을 생성하였습니다.")
			.build();
	}

	@Operation(summary = "수업 일정을 등록한다.", responses = {
		@ApiResponse(responseCode = "200", description = "수업 일정 등록 완료"),
		@ApiResponse(responseCode = "404", description = "트레이너를 찾을 수 없습니다.")
	})
	@PostMapping
	public ResponseHandler<Boolean> registerSchedule(@RequestBody @Valid ScheduleRegisterCommand request) {
		return ResponseHandler.<Boolean>builder()
			.statusCode(HttpStatus.OK)
			.data(scheduleService.registerSchedule(request))
			.message("일정 등록이 완료되었습니다.")
			.build();
	}

	@Operation(summary = "전체 일정을 조회한다.", responses = {
		@ApiResponse(responseCode = "200", description = "전체 일정 조회 완료")
	})
	@PostMapping("/find")
	public ResponseHandler<List<ScheduleCommandResult>> findAllSchedule(@RequestBody ScheduleSearchCond searchCond) {
		return ResponseHandler.<List<ScheduleCommandResult>>builder()
			.statusCode(HttpStatus.OK)
			.data(scheduleService.findAllSchedule(searchCond))
			.message("전체 일정을 조회했습니다.")
			.build();
	}

	@Operation(summary = "내 수업을 조회한다.", responses = {
		@ApiResponse(responseCode = "200", description = "내 수업 조회 완료")
	})
	@GetMapping("/{memberId}")
	public ResponseHandler<List<ScheduleCommandResult>> findMySchedule(@Parameter(description = "사용자 아이디") @PathVariable Long memberId) {
		return ResponseHandler.<List<ScheduleCommandResult>>builder()
			.statusCode(HttpStatus.OK)
			.data(scheduleService.findAllByApplicantId(memberId))
			.message("내 수업을 조회하였습니다.")
			.build();
	}

	@Operation(summary = "수업을 신청한다.", responses = {
		@ApiResponse(responseCode = "200", description = "수업 신청 완료")
	})
	@PostMapping("/{scheduleId}")
	public ResponseHandler<Boolean> reserveSchedule(@Parameter(description = "수업 일정 아이디") @PathVariable Long scheduleId, @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
		return ResponseHandler.<Boolean>builder()
			.statusCode(HttpStatus.OK)
			.data(scheduleService.reserveSchedule(scheduleId, customMemberDetails.getMemberId()))
			.message("일정 신청되었습니다.")
			.build();
	}

	@Operation(summary = "수업 대기 신청을 한다.", responses = {
		@ApiResponse(responseCode = "200", description = "수업 대기 신청 완료")
	})
	@PostMapping("stand-by/{scheduleId}")
	public ResponseHandler<Boolean> registerStandBySchedule(@Parameter(description = "일정 아이디") @PathVariable Long scheduleId, @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
		return ResponseHandler.<Boolean>builder()
			.statusCode(HttpStatus.OK)
			.data(scheduleService.registerStandBySchedule(scheduleId, customMemberDetails.getMemberId()))
			.message("대기 신청 되었습니다.")
			.build();
	}

	@Operation(summary = "신청한 수업의 대기를 취소한다.", responses = {
		@ApiResponse(responseCode = "200", description = "수업 대기 취소 완료")
	})
	@DeleteMapping("stand-by/{scheduleId}")
	public ResponseHandler<Boolean> cancelStandBySchedule(@Parameter(description = "일정 아이디") @PathVariable Long scheduleId, @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
		return ResponseHandler.<Boolean>builder()
			.statusCode(HttpStatus.OK)
			.data(scheduleService.cancelStandBySchedule(scheduleId, customMemberDetails.getMemberId()))
			.message("대기 신청이 취소되었습니다.")
			.build();
	}

	@Operation(summary = "트레이너가 일정을 취소한다.", responses = {
		@ApiResponse(responseCode = "200", description = "해당 일정을 취소하였습니다."),
		@ApiResponse(responseCode = "404", description = "해당 일정이 존재하지 않습니다.")
	})
	@PatchMapping("/trainer/{scheduleId}")
	public ResponseHandler<Boolean> cancelScheduleForTrainer(@Parameter(description = "일정 아이디") @PathVariable Long scheduleId, @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
		return ResponseHandler.<Boolean>builder()
			.statusCode(HttpStatus.OK)
			.data(scheduleService.cancelTrainerSchedule(scheduleId, customMemberDetails.getMemberId()))
			.message("일정을 취소하였습니다.")
			.build();
	}

	@Operation(summary = "회원이 수업을 취소한다.", responses = {
		@ApiResponse(responseCode = "200", description = "해당 수업을 취소하였습니다."),
		@ApiResponse(responseCode = "404", description = "해당 수업이 존재하지 않습니다.")
	})
	@PatchMapping("/member/{scheduleId}")
	public ResponseHandler<Boolean> cancelScheduleForMember(@Parameter(description = "일정 아이디") @PathVariable Long scheduleId, @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
		return ResponseHandler.<Boolean>builder()
			.statusCode(HttpStatus.OK)
			.data(scheduleService.cancelMemberSchedule(scheduleId, customMemberDetails.getMemberId()))
			.message("수업을 취소하였습니다.")
			.build();
	}
}