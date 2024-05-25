package com.tobe.healthy.member.presentation;

import com.tobe.healthy.common.CustomPaging;
import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.course.application.CourseService;
import com.tobe.healthy.course.domain.dto.out.CourseGetResult;
import com.tobe.healthy.diet.application.DietService;
import com.tobe.healthy.diet.domain.dto.DietDto;
import com.tobe.healthy.member.application.MemberService;
import com.tobe.healthy.member.domain.dto.out.MemberInfoResult;
import com.tobe.healthy.member.domain.dto.out.TrainerMappingResult;
import com.tobe.healthy.point.application.PointService;
import com.tobe.healthy.point.domain.dto.out.PointDto;
import com.tobe.healthy.workout.application.WorkoutHistoryService;
import com.tobe.healthy.workout.domain.dto.out.WorkoutHistoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public ResponseHandler<MemberInfoResult> getMemberInfo(@Parameter(description = "조회할 회원 아이디", example = "1")
														   @PathVariable("memberId") Long memberId) {
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
	public ResponseHandler<CustomPaging<WorkoutHistoryDto>> getWorkoutHistory(@AuthenticationPrincipal CustomMemberDetails loginMember,
																			  @Parameter(description = "조회할 날짜", example = "2024-12") @Param("searchDate") String searchDate,
																			  Pageable pageable) {
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
	public ResponseHandler<CustomPaging<WorkoutHistoryDto>> getWorkoutHistory(@AuthenticationPrincipal CustomMemberDetails loginMember,
																	  @Parameter(description = "학생 ID", example = "1") @PathVariable("memberId") Long memberId,
																	  @Parameter(description = "조회할 날짜", example = "2024-12") @Param("searchDate") String searchDate,
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
	public ResponseHandler<CustomPaging<DietDto>> getDiet(@AuthenticationPrincipal CustomMemberDetails loginMember,
														  @Parameter(description = "조회할 날짜", example = "2024-12") @Param("searchDate") String searchDate,
														  Pageable pageable) {
		return ResponseHandler.<CustomPaging<DietDto>>builder()
				.data(dietService.getDiet(loginMember.getMemberId(), pageable, searchDate))
				.message("식단기록 조회되었습니다.")
				.build();
	}

	@Operation(summary = "다른 학생의 식단기록 목록 조회", responses = {
			@ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
			@ApiResponse(responseCode = "200", description = "식단기록, 페이징을 반환한다.")
	})
	@GetMapping("/{memberId}/diets")
	public ResponseHandler<CustomPaging<DietDto>> getDiet(@Parameter(description = "학생 ID", example = "1") @PathVariable("memberId") Long memberId,
														  @Parameter(description = "조회할 날짜", example = "2024-12") @Param("searchDate") String searchDate,
														  Pageable pageable) {
		return ResponseHandler.<CustomPaging<DietDto>>builder()
				.data(dietService.getDiet(memberId, pageable, searchDate))
				.message("식단기록 조회되었습니다.")
				.build();
	}

	@Operation(summary = "내 트레이너가 관리하는 학생들의 식단기록 목록 조회하기", responses = {
			@ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
			@ApiResponse(responseCode = "200", description = "식단기록, 페이징을 반환한다.")
	})
	@GetMapping("/my-trainer/diets")
	public ResponseHandler<CustomPaging<DietDto>> getDietMyTrainer(@AuthenticationPrincipal CustomMemberDetails loginMember,
																   @Parameter(description = "조회할 날짜", example = "2024-12") @Param("searchDate") String searchDate,
																   Pageable pageable) {
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
	public ResponseHandler<CourseGetResult> getMyCourse(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
														@Parameter(description = "조회할 날짜", example = "2024-12") @Param("searchDate") String searchDate,
														Pageable pageable) {
		return ResponseHandler.<CourseGetResult>builder()
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
	public ResponseHandler<CourseGetResult> getCourse(@Parameter(description = "학생 ID") @PathVariable("memberId") Long memberId,
													  @AuthenticationPrincipal CustomMemberDetails customMemberDetails,
													  @Parameter(description = "조회할 날짜", example = "2024-12") @Param("searchDate") String searchDate,
													  Pageable pageable) {
		return ResponseHandler.<CourseGetResult>builder()
				.data(courseService.getCourse(customMemberDetails.getMember(), pageable, memberId, searchDate))
				.message("수강권이 조회되었습니다.")
				.build();
	}

	@Operation(summary = "학생이 본인의 포인트 조회", responses = {
			@ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
			@ApiResponse(responseCode = "200", description = "포인트 및 히스토리를 반환한다.")
	})
	@GetMapping("/point")
	public ResponseHandler<PointDto> getMyPoint(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
											  @Parameter(description = "조회할 날짜", example = "2024-12") @Param("searchDate") String searchDate,
											  Pageable pageable) {
		return ResponseHandler.<PointDto>builder()
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
	public ResponseHandler<PointDto> getPoint(@Parameter(description = "학생 ID") @PathVariable("memberId") Long memberId,
											  @Parameter(description = "조회할 날짜", example = "2024-12") @Param("searchDate") String searchDate,
											  Pageable pageable) {
		return ResponseHandler.<PointDto>builder()
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
	public ResponseHandler<Long> validateCurrentPassword(@RequestBody String password, @AuthenticationPrincipal CustomMemberDetails member) {
		return ResponseHandler.<Long>builder()
			.data(memberService.validateCurrentPassword(password, member.getMemberId()))
			.message("비밀번호가 확인되었습니다.")
			.build();
	}
}
