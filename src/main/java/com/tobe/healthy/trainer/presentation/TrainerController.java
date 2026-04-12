package com.tobe.healthy.trainer.presentation;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tobe.healthy.common.CustomPaging;
import com.tobe.healthy.ApiResult;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.diet.application.DietService;
import com.tobe.healthy.diet.presentation.dto.DietDto;
import com.tobe.healthy.member.application.MemberCommandService;
import com.tobe.healthy.member.presentation.dto.MemberDto;
import com.tobe.healthy.member.presentation.dto.out.MemberDetailResult;
import com.tobe.healthy.member.presentation.dto.out.MemberInTeamResult;
import com.tobe.healthy.member.domain.AlarmStatus;
import com.tobe.healthy.schedule.application.StudentScheduleService;
import com.tobe.healthy.schedule.presentation.dto.in.StudentScheduleCond;
import com.tobe.healthy.schedule.presentation.dto.out.MyReservationResponse;
import com.tobe.healthy.trainer.application.TrainerService;
import com.tobe.healthy.trainer.presentation.dto.TrainerMemberMappingDto;
import com.tobe.healthy.trainer.presentation.dto.in.MemberInviteCommand;
import com.tobe.healthy.trainer.presentation.dto.in.MemberLessonCommand;
import com.tobe.healthy.trainer.presentation.dto.out.MemberInviteResultCommand;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trainers")
@Tag(name = "05. 트레이너 API", description = "트레이너 API")
@Slf4j
public class TrainerController {

	private final TrainerService trainerService;
	private final StudentScheduleService studentScheduleService;
	private final MemberCommandService memberCommandService;
	private final DietService dietService;

	@Operation(summary = "트레이너가 학생 초대하기")
	@PostMapping("/invitation")
	@PreAuthorize("hasAuthority('ROLE_TRAINER')")
	public ApiResult<MemberInviteResultCommand> inviteMember(
		@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@RequestBody MemberInviteCommand command) {
		return ApiResult.<MemberInviteResultCommand>builder()
			.data(trainerService.inviteMember(command, customMemberDetails.getMember()))
			.message("회원초대가 완료 되었습니다.")
			.build();
	}

	@Operation(summary = "트레이너가 미가입 학생 직접 등록하기")
	@PostMapping("/nonmember")
	@PreAuthorize("hasAuthority('ROLE_TRAINER')")
	public ApiResult<MemberInviteResultCommand> inviteNonmember(
		@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@RequestBody MemberInviteCommand command) {
		return ApiResult.<MemberInviteResultCommand>builder()
			.data(trainerService.inviteNonmember(command, customMemberDetails.getMember()))
			.message("미가입 학생 등록이 완료 되었습니다.")
			.build();
	}

	@Operation(summary = "트레이너가 내 학생으로 등록하기")
	@PostMapping("/members/{memberId}")
	@PreAuthorize("hasAuthority('ROLE_TRAINER')")
	public ApiResult<TrainerMemberMappingDto> addStudentOfTrainer(
		@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@PathVariable @Parameter(description = "학생 ID") Long memberId,
		@RequestBody MemberLessonCommand command) {
		return ApiResult.<TrainerMemberMappingDto>builder()
			.data(trainerService.addStudentOfTrainer(customMemberDetails.getMember().getId(), memberId, command))
			.message("내 학생으로 등록되었습니다.")
			.build();
	}

	@Operation(summary = "트레이너가 내 학생을 삭제한다.")
	@DeleteMapping("/members/{memberId}")
	@PreAuthorize("hasAuthority('ROLE_TRAINER')")
	public ApiResult<TrainerMemberMappingDto> deleteStudentOfTrainer(
		@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@PathVariable @Parameter(description = "학생 ID") Long memberId) {
		trainerService.deleteStudentOfTrainer(customMemberDetails.getMember(), memberId);
		return ApiResult.<TrainerMemberMappingDto>builder()
			.message("내 학생에서 삭제되었습니다.")
			.build();
	}

	@Operation(summary = "트레이너가 학생 상세 조회")
	@GetMapping("/members/{memberId}")
	@PreAuthorize("hasAuthority('ROLE_TRAINER')")
	public ApiResult<MemberDetailResult> getMemberOfTrainer(
		@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@PathVariable @Parameter(description = "학생 ID") Long memberId) {
		return ApiResult.<MemberDetailResult>builder()
			.data(trainerService.getMemberOfTrainer(customMemberDetails.getMember(), memberId))
			.message("학생 상세가 조회되었습니다.")
			.build();
	}

	@Operation(summary = "트레이너가 관리하는 학생들을 조회한다.", description = "트레이너가 관리하는 학생 전체를 조회한다.")
	@GetMapping("/members")
	@PreAuthorize("hasAuthority('ROLE_TRAINER')")
	public ApiResult<List<MemberInTeamResult>> findAllMyMemberInTrainer(
		@AuthenticationPrincipal CustomMemberDetails member,
		@Parameter(description = "검색할 이름", example = "임채린")
		@RequestParam(required = false) String searchValue,
		@Parameter(description = "정렬 조건", example = "ranking, memberId")
		@RequestParam(required = false, defaultValue = "memberId") String sortValue,
		@PageableDefault(size = 100) Pageable pageable) {
		return ApiResult.<List<MemberInTeamResult>>builder()
			.data(trainerService.findAllMyMemberInTeam(member.getMemberId(), searchValue, sortValue, pageable))
			.message("트레이너가 관리하는 학생을 조회하였습니다.")
			.build();
	}

	@Operation(summary = "트레이너가 가입된(매핑 안 된) 학생들을 조회한다.", description = "트레이너가 가입된(매핑 안 된) 학생들을 조회한다.")
	@GetMapping("unattached-members")
	@PreAuthorize("hasAuthority('ROLE_TRAINER')")
	public ApiResult<List<MemberDto>> findAllUnattachedMembers(
		@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@Parameter(description = "검색할 이름", example = "임채린")
		@RequestParam(required = false) String searchValue,
		@Parameter(description = "정렬 조건", example = "memberId")
		@RequestParam(required = false, defaultValue = "memberId") String sortValue,
		Pageable pageable) {
		return ApiResult.<List<MemberDto>>builder()
			.data(trainerService.findAllUnattachedMembers(customMemberDetails.getMember(), searchValue, sortValue,
				pageable))
			.message("트레이너가 가입된 학생을 조회하였습니다.")
			.build();
	}

	@Operation(summary = "수업 기록 여부를 변경한다.", description = "트레이너가 사용하는 수업기록여부를 변경한다.")
	@PatchMapping("/trainer-feedback")
	@PreAuthorize("hasAuthority('ROLE_TRAINER')")
	public ApiResult<Boolean> changeTrainerFeedback(@Parameter(description = "변경할 수업 기록 상태", example = "ENABLED")
		@RequestParam AlarmStatus alarmStatus,
		@AuthenticationPrincipal CustomMemberDetails member) {
		return ApiResult.<Boolean>builder()
			.data(memberCommandService.changeTrainerFeedback(alarmStatus, member.getMemberId()))
			.message("수업 기록 여부가 변경되었습니다.")
			.build();
	}

	@Operation(summary = "트레이너가 관리하는 학생들의 식단기록 목록 조회하기")
	@GetMapping("/diets")
	@PreAuthorize("hasAuthority('ROLE_TRAINER')")
	public ApiResult<CustomPaging<DietDto>> getDietByTrainer(
		@AuthenticationPrincipal CustomMemberDetails loginMember,
		@Parameter(description = "조회할 날짜", example = "2024-12-01") @Param("searchDate") String searchDate,
		Pageable pageable) {
		return ApiResult.<CustomPaging<DietDto>>builder()
			.data(dietService.getDietByTrainer(loginMember.getMemberId(), pageable, searchDate))
			.message("식단기록이 조회되었습니다.")
			.build();
	}

	@Operation(summary = "트레이너가 학생의 다가오는 예약을 조회한다.", description = "트레이너가 학생의 다가오는 예약을 조회한다.")
	@GetMapping("/reservation/new")
	@PreAuthorize("hasAuthority('ROLE_TRAINER')")
	public ApiResult<MyReservationResponse> findNewReservationByTrainer(
		@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@ParameterObject StudentScheduleCond searchCond,
		@Param("memberId") Long memberId) {
		return ApiResult.<MyReservationResponse>builder()
			.data(studentScheduleService.findNewReservationByTrainer(customMemberDetails.getMemberId(), memberId,
				searchCond))
			.message("학생이 내 예약을 조회하였습니다.")
			.build();
	}

	@Operation(summary = "트레이너가 학생의 지난 예약을 조회한다.", description = "트레이너가 학생의 지난 예약을 조회한다.")
	@GetMapping("/reservation/old")
	@PreAuthorize("hasAuthority('ROLE_TRAINER')")
	public ApiResult<MyReservationResponse> findOldReservationByTrainer(
		@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@Parameter(description = "조회할 날짜", example = "2024-12") @Param("searchDate") String searchDate,
		@Param("memberId") Long memberId) {
		return ApiResult.<MyReservationResponse>builder()
			.data(studentScheduleService.findOldReservationByTrainer(customMemberDetails.getMemberId(), memberId,
				searchDate))
			.message("학생의 예약을 조회하였습니다.")
			.build();
	}

	@Operation(summary = "트레이너가 내 학생을 환불한다.")
	@DeleteMapping("/members/{memberId}/refund")
	@PreAuthorize("hasAuthority('ROLE_TRAINER')")
	public ApiResult<TrainerMemberMappingDto> refundStudentOfTrainer(
		@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@Parameter(description = "학생 ID") @PathVariable("memberId") Long memberId) {
		trainerService.refundStudentOfTrainer(customMemberDetails.getMember(), memberId);
		return ApiResult.<TrainerMemberMappingDto>builder()
			.message("환불이 완료되었습니다.")
			.build();
	}

}