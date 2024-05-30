package com.tobe.healthy.member.presentation;

import com.tobe.healthy.common.CustomPaging;
import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.course.application.CourseService;
import com.tobe.healthy.diet.application.DietService;
import com.tobe.healthy.diet.domain.dto.DietDto;
import com.tobe.healthy.member.application.MemberService;
import com.tobe.healthy.member.domain.dto.in.ValidateCurrentPassword;
import com.tobe.healthy.member.domain.dto.out.MemberInfoResult;
import com.tobe.healthy.member.domain.dto.out.TrainerMappingResult;
import com.tobe.healthy.point.application.PointService;
import com.tobe.healthy.workout.application.WorkoutHistoryService;
import com.tobe.healthy.workout.domain.dto.out.WorkoutHistoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members/v1")
@Slf4j
@Valid
@Tag(name = "02. 회원 API", description = "인증이 있어야만 접근 가능한 회원 API")
public class MemberController {

	private final MemberService memberService;
	private final WorkoutHistoryService workoutService;
	private final CourseService courseService;
	private final PointService pointService;
	private final DietService dietService;

	@Operation(summary = "내 정보 조회", responses = {
			@ApiResponse(responseCode = "400", description = "잘못된 요청."),
			@ApiResponse(responseCode = "200", description = "회원 정보가 조회되었습니다.")
	})
	@GetMapping("/me")
	public ResponseHandler<MemberInfoResult> getMemberInfo(@AuthenticationPrincipal CustomMemberDetails member) {
		return ResponseHandler.<MemberInfoResult>builder()
				.data(memberService.getMemberInfo(member.getMemberId()))
				.message("회원정보가 조회 되었습니다.")
				.build();
	}

	@Operation(summary = "회원 정보조회", responses = {
			@ApiResponse(responseCode = "400", description = "잘못된 요청."),
			@ApiResponse(responseCode = "200", description = "회원 정보가 조회되었습니다.")
	})
	@GetMapping("/{memberId}")
	public ResponseHandler<MemberInfoResult> getMemberInfo(@PathVariable Long memberId) {
		return ResponseHandler.<MemberInfoResult>builder()
				.data(memberService.getMemberInfo(memberId))
				.message("회원정보가 조회 되었습니다.")
				.build();
	}

	/**
	 * ============================== 운동기록 시작 ==============================
	 */
	@Operation(summary = "내 운동기록 목록 조회", responses = {
			@ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
			@ApiResponse(responseCode = "200", description = "운동기록, 페이징을 반환한다.")
	})
	@GetMapping("/me/workout-histories")
	public ResponseHandler<CustomPaging<WorkoutHistoryDto>> getWorkoutHistory(String searchDate,
																			  Pageable pageable,
																			  @AuthenticationPrincipal CustomMemberDetails loginMember) {
		return ResponseHandler.<CustomPaging<WorkoutHistoryDto>>builder()
				.data(workoutService.getWorkoutHistory(loginMember.getMember(), loginMember.getMemberId(), pageable, searchDate))
				.message("운동기록이 조회되었습니다.")
				.build();
	}

	@Operation(summary = "학생의 운동기록 목록 조회", responses = {
			@ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
			@ApiResponse(responseCode = "200", description = "운동기록, 페이징을 반환한다.")
	})
	@GetMapping("/{memberId}/workout-histories")
	public ResponseHandler<CustomPaging<WorkoutHistoryDto>> getWorkoutHistory(@PathVariable Long memberId, String searchDate,
																			  @AuthenticationPrincipal CustomMemberDetails loginMember,
																			  Pageable pageable) {
		return ResponseHandler.<CustomPaging<WorkoutHistoryDto>>builder()
				.data(workoutService.getWorkoutHistory(loginMember.getMember(), memberId, pageable, searchDate))
				.message("운동기록이 조회되었습니다.")
				.build();
	}

	/**
	 * ============================== 운동기록 종료 ==============================
	 */

	/**
	 * ============================== 식단기록 시작 ==============================
	 */
	@Operation(summary = "내 식단기록 목록 조회", responses = {
			@ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
			@ApiResponse(responseCode = "200", description = "식단기록, 페이징을 반환한다.")
	})
	@GetMapping("/me/diets")
	public ResponseHandler<CustomPaging<DietDto>> getDiet(String searchDate,
														  Pageable pageable,
														  @AuthenticationPrincipal CustomMemberDetails loginMember) {
		return ResponseHandler.<CustomPaging<DietDto>>builder()
				.data(dietService.getDiet(loginMember.getMemberId(), loginMember.getMemberId(), pageable, searchDate))
				.message("식단기록 조회되었습니다.")
				.build();
	}

	@Operation(summary = "다른 학생의 식단기록 목록 조회", responses = {
			@ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
			@ApiResponse(responseCode = "200", description = "식단기록, 페이징을 반환한다.")
	})
	@GetMapping("/{memberId}/diets")
	public ResponseHandler<CustomPaging<DietDto>> getDiet(@AuthenticationPrincipal CustomMemberDetails loginMember,
														  @PathVariable Long memberId,
														  String searchDate,
														  Pageable pageable) {
		return ResponseHandler.<CustomPaging<DietDto>>builder()
				.data(dietService.getDiet(loginMember.getMemberId(), memberId, pageable, searchDate))
				.message("식단기록 조회되었습니다.")
				.build();
	}

	@Operation(summary = "내 트레이너가 관리하는 학생들의 식단기록 목록 조회하기", responses = {
			@ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
			@ApiResponse(responseCode = "200", description = "식단기록, 페이징을 반환한다.")
	})
	@GetMapping("/my-trainer/diets")
	public ResponseHandler<CustomPaging<DietDto>> getDietMyTrainer(String searchDate,
																   Pageable pageable,
																   @AuthenticationPrincipal CustomMemberDetails loginMember) {
		return ResponseHandler.<CustomPaging<DietDto>>builder()
				.data(dietService.getDietMyTrainer(loginMember.getMemberId(), pageable, searchDate))
				.message("식단기록 조회되었습니다.")
				.build();
	}
	/**
	 * ============================== 식단기록 종료 ==============================
	 */

	@Operation(summary = "학생이 본인의 수강권 조회", responses = {
			@ApiResponse(responseCode = "404", description = "존재하지 않는 학생"),
			@ApiResponse(responseCode = "200", description = "수강권 정보를 반환한다.")
	})
	@GetMapping("/course")
	public ResponseHandler<CustomPaging> getMyCourse(String searchDate,
														Pageable pageable,
														@AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
		return ResponseHandler.<CustomPaging>builder()
				.data(courseService.getCourse(customMemberDetails.getMember(), pageable, customMemberDetails.getMemberId(), searchDate))
				.message("수강권이 조회되었습니다.")
				.build();
	}

	@Operation(summary = "트레이너가 학생의 수강권 조회", responses = {
			@ApiResponse(responseCode = "404", description = "존재하지 않는 학생"),
			@ApiResponse(responseCode = "200", description = "수강권 정보를 반환한다.")
	})
	@GetMapping("/{memberId}/course")
	@PreAuthorize("hasAuthority('ROLE_TRAINER')")
	public ResponseHandler<CustomPaging> getCourse(@PathVariable Long memberId,
													  String searchDate,
													  Pageable pageable,
													  @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
		return ResponseHandler.<CustomPaging>builder()
				.data(courseService.getCourse(customMemberDetails.getMember(), pageable, memberId, searchDate))
				.message("수강권이 조회되었습니다.")
				.build();
	}

	@Operation(summary = "학생이 본인의 포인트 조회", responses = {
			@ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
			@ApiResponse(responseCode = "200", description = "포인트 및 히스토리를 반환한다.")
	})
	@GetMapping("/point")
	public ResponseHandler<CustomPaging> getMyPoint(@AuthenticationPrincipal CustomMemberDetails customMemberDetails, String searchDate, Pageable pageable) {
		return ResponseHandler.<CustomPaging>builder()
				.data(pointService.getPoint(customMemberDetails.getMember().getId(), searchDate, pageable))
				.message("포인트가 조회되었습니다.")
				.build();
	}

	@Operation(summary = "트레이너가 학생의 포인트 조회", responses = {
			@ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
			@ApiResponse(responseCode = "200", description = "포인트 및 히스토리를 반환한다.")
	})
	@GetMapping("/{memberId}/point")
	@PreAuthorize("hasAuthority('ROLE_TRAINER')")
	public ResponseHandler<CustomPaging> getPoint(@PathVariable Long memberId, String searchDate, Pageable pageable) {
		return ResponseHandler.<CustomPaging>builder()
				.data(pointService.getPoint(memberId, searchDate, pageable))
				.message("포인트가 조회되었습니다.")
				.build();
	}

	@Operation(summary = "학생이 트레이너와 매핑 여부 조회", responses = {
			@ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
			@ApiResponse(responseCode = "200", description = "학생이 트레이너와 매핑 여부를 반환한다.")
	})
	@GetMapping("/trainer-mapping")
	public ResponseHandler<TrainerMappingResult> getTrainerMapping(@AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
		return ResponseHandler.<TrainerMappingResult>builder()
				.data(memberService.getTrainerMapping(customMemberDetails.getMember()))
				.message("매핑 여부가 조회되었습니다.")
				.build();
	}

	@Operation(summary = "회원이 현재 비밀번호를 검증한다.",
		responses = {
			@ApiResponse(responseCode = "404", description = "현재 비밀번호가 일치하지 않습니다."),
			@ApiResponse(responseCode = "200", description = "현재 비밀번호가 확인되었습니다.")
		})
	@PostMapping("/password")
	public ResponseHandler<Boolean> validateCurrentPassword(@RequestBody @Valid ValidateCurrentPassword request,
														 	@AuthenticationPrincipal CustomMemberDetails member) {
		return ResponseHandler.<Boolean>builder()
			.data(memberService.validateCurrentPassword(request, member.getMemberId()))
			.message("비밀번호가 확인되었습니다.")
			.build();
	}
}
