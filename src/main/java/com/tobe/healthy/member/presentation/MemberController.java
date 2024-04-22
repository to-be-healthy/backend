package com.tobe.healthy.member.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.course.application.CourseService;
import com.tobe.healthy.course.domain.dto.out.CourseGetResult;
import com.tobe.healthy.diet.application.DietService;
import com.tobe.healthy.diet.domain.dto.DietDto;
import com.tobe.healthy.member.application.MemberService;
import com.tobe.healthy.member.domain.dto.in.MemberPasswordChangeCommand;
import com.tobe.healthy.member.domain.dto.in.MemoCommand;
import com.tobe.healthy.member.domain.dto.out.MemberInfoResult;
import com.tobe.healthy.member.domain.entity.AlarmStatus;
import com.tobe.healthy.point.application.PointService;
import com.tobe.healthy.point.domain.dto.out.PointGetResult;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

	@Operation(summary = "회원 탈퇴한다.", description = "로그인한 계정의 현재 비밀번호와 일치하다면 회원탈퇴를 시킨다.",
		responses = {
			@ApiResponse(responseCode = "404", description = "등록된 회원이 아닙니다."),
			@ApiResponse(responseCode = "400", description = "확인 비밀번호가 일치하지 않습니다."),
			@ApiResponse(responseCode = "200", description = "회원 탈퇴 되었습니다.")
	})
	@PostMapping("/delete")
	public ResponseHandler<String> deleteMember(@Parameter(description = "현재 비밀번호", example = "zxcvbnm11") @RequestParam String password,
												@AuthenticationPrincipal CustomMemberDetails member) {
		return ResponseHandler.<String>builder()
				.data(memberService.deleteMember(password, member.getMemberId()))
				.message("회원 탈퇴 되었습니다.")
				.build();
	}

	@Operation(summary = "회원이 비밀번호를 변경한다.",
		responses = {
			@ApiResponse(responseCode = "404", description = "등록된 회원이 아닙니다."),
			@ApiResponse(responseCode = "400", description = "확인 비밀번호가 일치하지 않습니다."),
			@ApiResponse(responseCode = "200", description = "비밀번호 변경이 완료 되었습니다.")
	})
	@PatchMapping("/password")
	public ResponseHandler<Boolean> changePassword(@RequestBody MemberPasswordChangeCommand request,
												   @AuthenticationPrincipal CustomMemberDetails member) {
		return ResponseHandler.<Boolean>builder()
				.data(memberService.changePassword(request, member.getMemberId()))
				.message("비밀번호 변경이 완료되었습니다.")
				.build();
	}

	@Operation(summary = "프로필 사진을 등록한다.", responses = {
			@ApiResponse(responseCode = "404", description = "등록된 회원이 아닙니다."),
			@ApiResponse(responseCode = "500", description = "파일 업로드중 에러가 발생하였습니다."),
			@ApiResponse(responseCode = "200", description = "프로필 사진이 등록되었습니다.")
	})
	@PutMapping("/profile")
	public ResponseHandler<Boolean> changeProfile(@RequestParam MultipartFile file,
												  @AuthenticationPrincipal CustomMemberDetails member) {
		return ResponseHandler.<Boolean>builder()
				.data(memberService.changeProfile(file, member.getMemberId()))
				.message("프로필 사진이 등록되었습니다.")
				.build();
	}

	@Operation(summary = "회원이 이름을 변경한다.", responses = {
			@ApiResponse(responseCode = "404", description = "등록된 회원이 아닙니다."),
			@ApiResponse(responseCode = "200", description = "이름이 변경되었습니다.")
	})
	@PatchMapping("/name")
	public ResponseHandler<Boolean> changeName(@Parameter(description = "변경할 이름", example = "홍길동") @RequestParam String name,
											   @AuthenticationPrincipal CustomMemberDetails member) {
		return ResponseHandler.<Boolean>builder()
				.data(memberService.changeName(name, member.getMemberId()))
				.message("이름이 변경되었습니다.")
				.build();
	}

	@Operation(summary = "알림 상태를 변경한다.", responses = {
			@ApiResponse(responseCode = "404", description = "등록된 회원이 아닙니다."),
			@ApiResponse(responseCode = "200", description = "알림 상태가 변경되었습니다.")
	})
	@PatchMapping("/alarm")
	public ResponseHandler<Boolean> changeAlarm(@Parameter(description = "변경할 알림 상태", example = "ENABLED") @RequestParam AlarmStatus alarmStatus,
												@AuthenticationPrincipal CustomMemberDetails member) {
		return ResponseHandler.<Boolean>builder()
				.data(memberService.changeAlarm(alarmStatus, member.getMemberId()))
				.message("알림 상태가 변경되었습니다.")
				.build();
	}

	@Operation(summary = "학생의 운동기록 목록 조회", responses = {
			@ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
			@ApiResponse(responseCode = "200", description = "운동기록, 페이징을 반환한다.")
	})
	@GetMapping("/{memberId}/workout-histories")
	public ResponseHandler<List<WorkoutHistoryDto>> getWorkoutHistory(@AuthenticationPrincipal CustomMemberDetails loginMember,
																	  @Parameter(description = "학생 ID", example = "1") @PathVariable("memberId") Long memberId,
																	  @Parameter(description = "조회할 날짜", example = "2024-12") @Param("searchDate") String searchDate,
																	  Pageable pageable) {
		return ResponseHandler.<List<WorkoutHistoryDto>>builder()
				.data(workoutService.getWorkoutHistory(loginMember.getMember(), memberId, pageable, searchDate))
				.message("운동기록이 조회되었습니다.")
				.build();
	}

	@Operation(summary = "학생의 식단기록 목록 조회", responses = {
			@ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
			@ApiResponse(responseCode = "200", description = "식단기록, 페이징을 반환한다.")
	})
	@GetMapping("/{memberId}/diets")
	public ResponseHandler<List<DietDto>> getDiet(@Parameter(description = "학생 ID", example = "1") @PathVariable("memberId") Long memberId,
																	  @Parameter(description = "조회할 날짜", example = "2024-12") @Param("searchDate") String searchDate,
																	  Pageable pageable) {
		return ResponseHandler.<List<DietDto>>builder()
				.data(dietService.getDiet(memberId, pageable, searchDate))
				.message("식단기록 조회되었습니다.")
				.build();
	}

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

	@Operation(summary = "학생의 포인트 조회", responses = {
			@ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
			@ApiResponse(responseCode = "200", description = "포인트 및 히스토리를 반환한다.")
	})
	@GetMapping("/point")
	public ResponseHandler<PointGetResult> getPoint(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
													@Parameter(description = "조회할 날짜", example = "2024-12") @Param("searchDate") String searchDate,
													Pageable pageable) {
		return ResponseHandler.<PointGetResult>builder()
				.data(pointService.getPoint(customMemberDetails.getMember(), searchDate, pageable))
				.message("포인트가 조회되었습니다.")
				.build();
	}

	@Operation(summary = "트레이너가 학생의 메모를 수정한다.", responses = {
			@ApiResponse(responseCode = "404", description = "등록된 학생이 아닙니다."),
			@ApiResponse(responseCode = "200", description = "메모가 수정되었습니다.")
	})
	@PutMapping("/{memberId}/memo")
	@PreAuthorize("hasAuthority('ROLE_TRAINER')")
	public ResponseHandler<Void> updateMemo(@AuthenticationPrincipal CustomMemberDetails trainer,
											   @PathVariable Long memberId,
											   @RequestBody MemoCommand command) {
		memberService.updateMemo(trainer.getMemberId(), memberId, command);
		return ResponseHandler.<Void>builder()
				.message("메모가 수정되었습니다.")
				.build();
	}

	@Operation(summary = "트레이너가 학생의 닉네임을 지정한다.", responses = {
			@ApiResponse(responseCode = "404", description = "등록된 학생이 아닙니다."),
			@ApiResponse(responseCode = "200", description = "닉네임이 설정되었습니다.")
	})
	@PostMapping("/nickname/{studentId}")
	@PreAuthorize("hasAuthority('ROLE_TRAINER')")
	public ResponseHandler<Boolean> assignNickname(@PathVariable Long studentId,
												   @Parameter(description = "등록할 닉네임", example = "홍박사") @RequestParam String nickname) {
		return ResponseHandler.<Boolean>builder()
				.data(memberService.assignNickname(nickname, studentId))
				.message("닉네임을 지정하였습니다.")
				.build();
	}

}
