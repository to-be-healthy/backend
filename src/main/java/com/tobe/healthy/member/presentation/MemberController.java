package com.tobe.healthy.member.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.member.application.MemberService;
import com.tobe.healthy.member.domain.dto.in.MemberJoinCommand;
import com.tobe.healthy.member.domain.dto.in.MemberPasswordChangeCommand;
import com.tobe.healthy.member.domain.dto.out.InvitationMappingResult;
import com.tobe.healthy.member.domain.dto.out.MemberJoinCommandResult;
import com.tobe.healthy.member.domain.entity.AlarmStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member/v1")
@Slf4j
@Valid
@Tag(name = "회원 API", description = "인증이 있어야만 접근 가능한 회원 API")
public class MemberController {

	private final MemberService memberService;

	@Operation(summary = "회원 탈퇴한다.", responses = {
		@ApiResponse(responseCode = "404", description = "등록된 회원이 아닙니다."),
		@ApiResponse(responseCode = "400", description = "확인 비밀번호가 일치하지 않습니다."),
		@ApiResponse(responseCode = "200", description = "회원 탈퇴 되었습니다.")
	})
	@PostMapping("/delete")
	public ResponseHandler<String> deleteMember(@Parameter(description = "비밀번호") @RequestParam String password,
												@AuthenticationPrincipal CustomMemberDetails member) {
		return ResponseHandler.<String>builder()
			.data(memberService.deleteMember(password, member.getMemberId()))
			.message("회원탈퇴 되었습니다.")
			.build();
	}

	@Operation(summary = "비밀번호를 변경한다.", responses = {
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

	@Operation(summary = "프로필 사진이 등록되었습니다.", responses = {
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

	@Operation(summary = "이름이 변경되었습니다.", responses = {
		@ApiResponse(responseCode = "404", description = "등록된 회원이 아닙니다."),
		@ApiResponse(responseCode = "200", description = "이름이 변경되었습니다.")
	})
	@PatchMapping("/name")
	public ResponseHandler<Boolean> changeName(@Parameter(description = "변경할 이름") @RequestParam String name,
											   @AuthenticationPrincipal CustomMemberDetails member) {
		return ResponseHandler.<Boolean>builder()
			.data(memberService.changeName(name, member.getMemberId()))
			.message("이름이 변경되었습니다.")
			.build();
	}

	@Operation(summary = "알림 상태가 변경되었습니다.", responses = {
		@ApiResponse(responseCode = "404", description = "등록된 회원이 아닙니다."),
		@ApiResponse(responseCode = "200", description = "알림 상태가 변경되었습니다.")
	})
	@PatchMapping("/alarm")
	public ResponseHandler<Boolean> changeAlarm(@Parameter(description = "변경할 알림 상태") @RequestParam AlarmStatus alarmStatus,
												@AuthenticationPrincipal CustomMemberDetails member) {
		return ResponseHandler.<Boolean>builder()
			.data(memberService.changeAlarm(alarmStatus, member.getMemberId()))
			.message("알림 상태가 변경되었습니다.")
			.build();
	}

	@Operation(summary = "수업 기록 여부가 변경되었습니다.", responses = {
		@ApiResponse(responseCode = "404", description = "등록된 회원이 아닙니다."),
		@ApiResponse(responseCode = "200", description = "수업 기록 여부가 변경되었습니다.")
	})
	@PatchMapping("/trainer-feedback")
	@PreAuthorize("hasAuthority('TRAINER')")
	public ResponseHandler<Boolean> changeTrainerFeedback(@Parameter(description = "변경할 수업 기록 상태") @RequestParam AlarmStatus alarmStatus,
														  @AuthenticationPrincipal CustomMemberDetails member) {
		return ResponseHandler.<Boolean>builder()
			.data(memberService.changeTrainerFeedback(alarmStatus, member.getMemberId()))
			.message("수업 기록 여부가 변경되었습니다.")
			.build();
	}

	@Operation(summary = "초대링크 uuid 데이터 조회", responses = {
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "200", description = "성공")
	})
	@GetMapping("/invitation/uuid")
	public ResponseHandler<InvitationMappingResult> getInvitationMapping(@RequestParam String uuid) {
		return ResponseHandler.<InvitationMappingResult>builder()
			.data(memberService.getInvitationMapping(uuid))
			.message("조회가 완료되었습니다.")
			.build();
	}

	@Operation(summary = "초대링크 회원가입", responses = {
		@ApiResponse(responseCode = "401", description = "이미 등록된 이메일입니다."),
		@ApiResponse(responseCode = "405", description = "이미 등록된 닉네임입니다."),
		@ApiResponse(responseCode = "200", description = "회원가입에 성공하였습니다.")
	})
	@PostMapping("/invitation/join")
	public ResponseHandler<MemberJoinCommandResult> joinWithInvitation(@RequestBody MemberJoinCommand request) {
		return ResponseHandler.<MemberJoinCommandResult>builder()
			.data(memberService.joinWithInvitation(request))
			.message("회원가입이 완료되었습니다.")
			.build();
	}
}