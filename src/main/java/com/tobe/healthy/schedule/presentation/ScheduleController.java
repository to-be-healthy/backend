package com.tobe.healthy.schedule.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.schedule.application.ScheduleService;
import com.tobe.healthy.schedule.domain.dto.in.AutoCreateScheduleCommand;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleRegisterCommand;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleSearchCond;
import com.tobe.healthy.schedule.domain.dto.out.MyReservationResponse;
import com.tobe.healthy.schedule.domain.dto.out.MyStandbyScheduleResponse;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleInfo;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule/v1")
@Slf4j
@Valid
@Tag(name = "03-01.수업 API", description = "수업 일정 API")
public class ScheduleController {

	private final ScheduleService scheduleService;

	@Operation(summary = "트레이너가 자동으로 수업 일정을 생성한다.", description = "트레이너가 자동으로 수업 일정을 생성한다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "자동 수업 일정 생성 완료")
	})
	@PreAuthorize("hasAuthority('ROLE_TRAINER')")
	@PostMapping
	public ResponseHandler<TreeMap<LocalDate, ArrayList<ScheduleInfo>>> createSchedule(@RequestBody AutoCreateScheduleCommand request) {
		return ResponseHandler.<TreeMap<LocalDate, ArrayList<ScheduleInfo>>>builder()
				.data(scheduleService.autoCreateSchedule(request))
				.message("자동으로 일정을 생성하였습니다.")
				.build();
	}

	@Operation(summary = "트레이너가 일정을 등록한다.", description = "트레이너가 수업 일정을 등록한다. 사전 등록할 회원이 있으면 포함해서 등록한다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "수업 일정 등록 완료"),
			@ApiResponse(responseCode = "404", description = "트레이너를 찾을 수 없습니다.")
	})
	@PostMapping("/register")
	@PreAuthorize("hasAuthority('ROLE_TRAINER')")
	public ResponseHandler<Boolean> registerSchedule(@RequestBody ScheduleRegisterCommand request,
													 @AuthenticationPrincipal CustomMemberDetails member) {
		return ResponseHandler.<Boolean>builder()
				.data(scheduleService.registerSchedule(request, member.getMemberId()))
				.message("일정 등록이 완료되었습니다.")
				.build();
	}

	@Operation(summary = "전체 일정을 조회한다.", description = "전체 일정을 조회한다. 특정 일자나 기간으로 조회하고 싶으면 DTO를 활용한다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "전체 일정 조회 완료")
	})
	@GetMapping("/all")
	public ResponseHandler<List<ScheduleCommandResult>> findAllSchedule(@ParameterObject ScheduleSearchCond searchCond) {
		return ResponseHandler.<List<ScheduleCommandResult>>builder()
				.data(scheduleService.findAllSchedule(searchCond))
				.message("전체 일정을 조회했습니다.")
				.build();
	}

	@Operation(summary = "학생이 내 수업을 조회한다.", description = "회원이 등록된 수업 전체를 조회한다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "내 수업 조회 완료")
	})
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_STUDENT')")
	public ResponseHandler<List<ScheduleCommandResult>> findMySchedule(@AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
		return ResponseHandler.<List<ScheduleCommandResult>>builder()
				.data(scheduleService.findAllByApplicantId(customMemberDetails.getMemberId()))
				.message("내 수업을 조회하였습니다.")
				.build();
	}

	@Operation(summary = "학생이 수업을 신청한다.", description = "학생이 수업을 신청한다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "수업 신청 완료")
	})
	@PreAuthorize("hasAuthority('ROLE_STUDENT')")
	@PostMapping("/{scheduleId}")
	public ResponseHandler<Boolean> reserveSchedule(@Parameter(description = "수업 일정 아이디") @PathVariable Long scheduleId,
													@AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
		return ResponseHandler.<Boolean>builder()
				.data(scheduleService.reserveSchedule(scheduleId, customMemberDetails.getMemberId()))
				.message("일정 신청되었습니다.")
				.build();
	}

	@Operation(summary = "학생이 수업을 취소한다.", description = "학생이 등록한 수업을 취소한다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "해당 수업을 취소하였습니다."),
			@ApiResponse(responseCode = "404", description = "해당 수업이 존재하지 않습니다.")
	})
	@PreAuthorize("hasAuthority('ROLE_STUDENT')")
	@DeleteMapping("/{scheduleId}")
	public ResponseHandler<Boolean> cancelScheduleForMember(@Parameter(description = "일정 아이디", example = "1") @PathVariable Long scheduleId,
															@AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
		return ResponseHandler.<Boolean>builder()
				.data(scheduleService.cancelMemberSchedule(scheduleId, customMemberDetails.getMemberId()))
				.message("수업을 취소하였습니다.")
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
				.data(scheduleService.cancelTrainerSchedule(scheduleId, customMemberDetails.getMemberId()))
				.message("일정을 취소하였습니다.")
				.build();
	}

	@Operation(summary = "학생이 내 예약을 조회한다.", description = "학생이 내 예약을 조회한다.",
			responses = {
					@ApiResponse(responseCode = "200", description = "학생이 내 예약을 조회하였습니다.")
			})
	@GetMapping("/my-reservation")
	@PreAuthorize("hasAuthority('ROLE_STUDENT')")
	public ResponseHandler<List<MyReservationResponse>> findAllMyReservation(@AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
		return ResponseHandler.<List<MyReservationResponse>>builder()
				.data(scheduleService.findAllMyReservation(customMemberDetails.getMemberId()))
				.message("학생이 내 예약을 조회하였습니다.")
				.build();
	}

	@Operation(summary = "학생이 대기중인 예약을 조회한다.", description = "학생이 대기중인 예약을 조회한다.",
			responses = {
					@ApiResponse(responseCode = "200", description = "학생이 대기중인 예약을 조회하였습니다.")
			})
	@GetMapping("/my-standby")
	@PreAuthorize("hasAuthority('ROLE_STUDENT')")
	public ResponseHandler<List<MyStandbyScheduleResponse>> findAllMyStandbySchedule(@AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
		return ResponseHandler.<List<MyStandbyScheduleResponse>>builder()
				.data(scheduleService.findAllMyStandbySchedule(customMemberDetails.getMemberId()))
				.message("학생이 대기중인 예약을 조회하였습니다.")
				.build();
	}
}
