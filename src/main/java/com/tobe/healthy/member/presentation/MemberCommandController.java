package com.tobe.healthy.member.presentation;

import static org.springframework.http.MediaType.*;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tobe.healthy.ApiResult;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.member.application.MemberCommandService;
import com.tobe.healthy.member.presentation.dto.in.CommandAssignNickname;
import com.tobe.healthy.member.presentation.dto.in.CommandChangeEmail;
import com.tobe.healthy.member.presentation.dto.in.CommandChangeMemberPassword;
import com.tobe.healthy.member.presentation.dto.in.CommandChangeName;
import com.tobe.healthy.member.presentation.dto.in.CommandUpdateMemo;
import com.tobe.healthy.member.presentation.dto.out.CommandAssignNicknameResult;
import com.tobe.healthy.member.presentation.dto.out.CommandChangeNameResult;
import com.tobe.healthy.member.presentation.dto.out.DeleteMemberProfileResult;
import com.tobe.healthy.member.presentation.dto.out.MemberChangeAlarmResult;
import com.tobe.healthy.member.presentation.dto.out.RegisterMemberProfileResult;
import com.tobe.healthy.member.domain.AlarmStatus;
import com.tobe.healthy.member.domain.AlarmType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@Slf4j
@Valid
@Tag(name = "02. 회원 API", description = "인증이 있어야만 접근 가능한 회원 API")
public class MemberCommandController {

	private final MemberCommandService memberCommandService;

	@Operation(summary = "로그아웃", description = "로그아웃시 refreshToken, fcmToken을 삭제한다.")
	@PostMapping("/logout")
	public void logout(@AuthenticationPrincipal CustomMemberDetails member) {
		memberCommandService.logout(member.getMemberId());
	}

	@Operation(summary = "회원 탈퇴한다.", description = "로그인한 계정의 현재 비밀번호와 일치하다면 회원탈퇴를 시킨다.")
	@PostMapping("/delete")
	public ApiResult<String> deleteMember(@AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
		return ApiResult.<String>builder()
			.data(memberCommandService.deleteMember(customMemberDetails.getMember()))
			.message("회원 탈퇴 되었습니다.")
			.build();
	}

	@Operation(summary = "회원이 비밀번호를 변경한다.")
	@PatchMapping("/password")
	public ApiResult<Boolean> changePassword(@RequestBody @Valid CommandChangeMemberPassword request,
		@AuthenticationPrincipal CustomMemberDetails member) {
		return ApiResult.<Boolean>builder()
			.data(memberCommandService.changePassword(request, member.getMemberId()))
			.message("비밀번호 변경이 완료되었습니다.")
			.build();
	}

	@Operation(summary = "프로필 사진을 등록한다.")
	@PutMapping(value = "/profile", consumes = MULTIPART_FORM_DATA_VALUE)
	public ApiResult<RegisterMemberProfileResult> changeProfile(@RequestParam MultipartFile file,
		@AuthenticationPrincipal CustomMemberDetails member) {
		return ApiResult.<RegisterMemberProfileResult>builder()
			.data(memberCommandService.registerProfile(file, member.getMemberId()))
			.message("프로필 사진이 등록되었습니다.")
			.build();
	}

	@Operation(summary = "프로필 사진을 삭제한다.")
	@DeleteMapping("/profile")
	public ApiResult<DeleteMemberProfileResult> changeProfile(
		@AuthenticationPrincipal CustomMemberDetails member) {
		return ApiResult.<DeleteMemberProfileResult>builder()
			.data(memberCommandService.deleteProfile(member.getMemberId()))
			.message("프로필 사진이 삭제되었습니다.")
			.build();
	}

	@Operation(summary = "회원이 이름을 변경한다.")
	@PatchMapping("/name")
	public ApiResult<CommandChangeNameResult> changeName(
		@RequestBody @Valid CommandChangeName request,
		@AuthenticationPrincipal CustomMemberDetails member
	) {

		return ApiResult.<CommandChangeNameResult>builder()
			.data(memberCommandService.changeName(request, member.getMemberId()))
			.message("이름이 변경되었습니다.")
			.build();
	}

	@Operation(summary = "알림 상태를 변경한다.")
	@PatchMapping("/alarm/{type}/{status}")
	public ApiResult<MemberChangeAlarmResult> changeAlarm(@PathVariable AlarmType type,
		@PathVariable AlarmStatus status,
		@AuthenticationPrincipal CustomMemberDetails member) {
		return ApiResult.<MemberChangeAlarmResult>builder()
			.data(memberCommandService.changeAlarm(type, status, member.getMemberId()))
			.message("알림 상태가 변경되었습니다.")
			.build();
	}

	@Operation(summary = "트레이너가 학생의 메모를 수정한다.")
	@PutMapping("/{memberId}/memo")
	@PreAuthorize("hasAuthority('ROLE_TRAINER')")
	public ApiResult<Void> updateMemo(@AuthenticationPrincipal CustomMemberDetails trainer,
		@PathVariable Long memberId,
		@RequestBody CommandUpdateMemo command) {
		memberCommandService.updateMemo(trainer.getMemberId(), memberId, command);
		return ApiResult.<Void>builder()
			.message("메모가 수정되었습니다.")
			.build();
	}

	@Operation(summary = "트레이너가 학생의 닉네임을 지정한다.")
	@PostMapping("/nickname/{studentId}")
	@PreAuthorize("hasAuthority('ROLE_TRAINER')")
	public ApiResult<CommandAssignNicknameResult> assignNickname(@PathVariable Long studentId,
		@RequestBody @Valid CommandAssignNickname request) {
		return ApiResult.<CommandAssignNicknameResult>builder()
			.data(memberCommandService.assignNickname(request, studentId))
			.message("닉네임을 지정하였습니다.")
			.build();
	}

	@Operation(summary = "회원이 이메일을 변경한다.")
	@PatchMapping("/email")
	public ApiResult<Boolean> changeEmail(@RequestBody @Valid CommandChangeEmail request,
		@AuthenticationPrincipal CustomMemberDetails member) {
		return ApiResult.<Boolean>builder()
			.data(memberCommandService.changeEmail(request, member.getMemberId()))
			.message("이메일이 변경되었습니다.")
			.build();
	}

	@Operation(summary = "스케줄 공지 보기 여부를 변경한다.", description = "스케줄 공지 보기 여부를 변경한다.")
	@PatchMapping("/schedule-notice")
	public ApiResult<Boolean> changeScheduleNotice(@RequestParam AlarmStatus alarmStatus,
		@AuthenticationPrincipal CustomMemberDetails member) {
		return ApiResult.<Boolean>builder()
			.data(memberCommandService.changeScheduleNotice(alarmStatus, member.getMemberId()))
			.message("스케줄 공지 보기 여부가 변경되었습니다.")
			.build();
	}

	@Operation(summary = "식단 공지 보기 여부를 변경한다.", description = "식단 공지 보기 여부를 변경한다.")
	@PatchMapping("/diet-notice")
	public ApiResult<Boolean> changeDietNotice(@RequestParam AlarmStatus alarmStatus,
		@AuthenticationPrincipal CustomMemberDetails member) {
		return ApiResult.<Boolean>builder()
			.data(memberCommandService.changeDietNotice(alarmStatus, member.getMemberId()))
			.message("식단 공지 보기 여부가 변경되었습니다.")
			.build();
	}
}