package com.tobe.healthy.member.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.member.application.MemberService;
import com.tobe.healthy.member.domain.dto.in.MemberFindIdCommand;
import com.tobe.healthy.member.domain.dto.in.MemberFindPWCommand;
import com.tobe.healthy.member.domain.dto.in.MemberJoinCommand;
import com.tobe.healthy.member.domain.dto.in.MemberLoginCommand;
import com.tobe.healthy.member.domain.dto.in.MemberPasswordChangeCommand;
import com.tobe.healthy.member.domain.dto.out.MemberJoinCommandResult;
import com.tobe.healthy.member.domain.entity.AlarmStatus;
import com.tobe.healthy.member.domain.entity.Tokens;
import com.tobe.healthy.member.domain.entity.TrainerFeedback;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Slf4j
@Tag(name = "01.Member", description = "회원 API")
public class MemberController {

	private final MemberService memberService;

	@Operation(summary = "아이디 중복 확인하기", responses = {
		@ApiResponse(responseCode = "400", description = "이미 등록된 아이디"),
		@ApiResponse(responseCode = "200", description = "사용 가능한 아이디")
	})
	@GetMapping("/userid")
	public ResponseHandler<Boolean> validateUsernameDuplication(@Parameter(description = "아이디") @RequestParam String userId) {
		return ResponseHandler.<Boolean>builder()
			.data(memberService.validateUserIdDuplication(userId))
			.message("사용 가능한 아이디입니다.")
			.build();
	}

	@Operation(summary = "이메일 중복을 확인한다.", responses = {
		@ApiResponse(responseCode = "400", description = "이미 등록된 이메일입니다."),
		@ApiResponse(responseCode = "200", description = "사용 가능한 이메일입니다.")
	})
	@GetMapping("/email")
	public ResponseHandler<Boolean> validateEmailDuplication(@Parameter(description = "이메일") @RequestParam String email) {
		return ResponseHandler.<Boolean>builder()
			.data(memberService.validateEmailDuplication(email))
			.message("사용 가능한 이메일입니다.")
			.build();
	}

	@Operation(summary = "이메일로 인증번호를 전송한다.", responses = {
		@ApiResponse(responseCode = "400", description = "이미 등록된 이메일입니다."),
		@ApiResponse(responseCode = "200", description = "이메일로 인증번호를 전송하였습니다.")
	})
	@PostMapping("/email")
	public ResponseHandler<String> sendEmailVerification(@Parameter(description = "이메일") @RequestParam String email) {
		return ResponseHandler.<String>builder()
			.data(memberService.sendEmailVerification(email))
			.message("이메일 인증번호가 전송되었습니다.")
			.build();
	}

	@Operation(summary = "이메일 인증번호를 검증한다.", responses = {
		@ApiResponse(responseCode = "400", description = "이메일 인증번호가 일치하지 않습니다."),
		@ApiResponse(responseCode = "200", description = "이메일 인증번호가 일치합니다.")
	})
	@Schema(name = "authNumber")
	@PostMapping("/email/{authNumber}")
	public ResponseHandler<Boolean> verifyAuthMail(@Parameter(description = "인증번호") @PathVariable String authNumber, @Parameter(description = "이메일") @RequestParam String email) {
		return ResponseHandler.<Boolean>builder()
			.data(memberService.verifyEmailAuthNumber(authNumber, email))
			.message("인증번호가 확인되었습니다.")
			.build();
	}

	@Operation(summary = "회원가입", responses = {
		@ApiResponse(responseCode = "401", description = "이미 등록된 이메일입니다."),
		@ApiResponse(responseCode = "405", description = "이미 등록된 닉네임입니다."),
		@ApiResponse(responseCode = "200", description = "회원가입에 성공하였습니다.")
	})
	@PostMapping("/join")
	public ResponseHandler<MemberJoinCommandResult> join(@RequestBody @Valid MemberJoinCommand request) {
		return ResponseHandler.<MemberJoinCommandResult>builder()
			.data(memberService.joinMember(request))
			.message("회원가입이 완료되었습니다.")
			.build();
	}

	@Operation(summary = "로그인", responses = {
		@ApiResponse(responseCode = "400", description = "아이디 또는 비밀번호가 잘못되었습니다."),
		@ApiResponse(responseCode = "200", description = "로그인에 성공하고, Access Token, Refresh Token을 반환한다.")
	})
	@PostMapping("/login")
	public ResponseHandler<Tokens> login(@RequestBody @Valid MemberLoginCommand request) {
		return ResponseHandler.<Tokens>builder()
			.data(memberService.login(request))
			.message("로그인 되었습니다.")
			.build();
	}

	@Operation(summary = "토큰을 갱신한다.", responses = {
		@ApiResponse(responseCode = "405", description = "Refresh Token 유효기간이 만료되었습니다."),
		@ApiResponse(responseCode = "406", description = "Refresh Token을 찾을 수 없습니다."),
		@ApiResponse(responseCode = "400", description = "회원을 찾을 수 없습니다."),
		@ApiResponse(responseCode = "200", description = "Access Token, Refresh Token을 반환한다.")
	})
	@PostMapping("/refresh-token")
	public ResponseHandler<Tokens> refreshToken(@Parameter(description = "아이디") @RequestParam String userId, @Parameter(description = "갱신 토큰") @RequestParam String refreshToken) {
		return ResponseHandler.<Tokens>builder()
			.data(memberService.refreshToken(userId, refreshToken))
			.message("토큰이 갱신되었습니다.")
			.build();
	}

	@Operation(summary = "아이디를 찾는다.", responses = {
		@ApiResponse(responseCode = "400", description = "등록된 회원이 아닙니다."),
		@ApiResponse(responseCode = "200", description = "휴대폰 번호, 닉네임에 일치하는 이메일을 반환한다.")
	})
	@PostMapping("/find/userId")
	public ResponseHandler<String> findUserId(@RequestBody @Valid MemberFindIdCommand request) {
		return ResponseHandler.<String>builder()
			.data(memberService.findUserId(request))
			.message("아이디 찾기에 성공하였습니다.")
			.build();
	}

	@Operation(summary = "비밀번호를 찾는다.", responses = {
		@ApiResponse(responseCode = "400", description = "등록된 회원이 아닙니다."),
		@ApiResponse(responseCode = "200", description = "등록된 이메일에 초기화 비밀번호를 전송한다.")
	})
	@PostMapping("/find/password")
	public ResponseHandler<String> findMemberPW(@RequestBody @Valid MemberFindPWCommand request) {
		return ResponseHandler.<String>builder()
			.data(memberService.findMemberPW(request))
			.message("이메일에 초기화 비밀번호가 전송되었습니다.")
			.build();
	}

	@Operation(summary = "회원 탈퇴한다.", responses = {
		@ApiResponse(responseCode = "400", description = "등록된 회원이 아닙니다."),
		@ApiResponse(responseCode = "200", description = "회원 탈퇴 되었습니다.")
	})
	@PostMapping("/delete")
	public ResponseHandler<String> deleteMember(@Parameter(description = "아이디") @RequestParam String userId, @Parameter(description = "비밀번호") @RequestParam String password) {
		return ResponseHandler.<String>builder()
			.data(memberService.deleteMember(userId, password))
			.message("회원탈퇴 되었습니다.")
			.build();
	}

	@Operation(summary = "비밀번호를 변경한다.", responses = {
		@ApiResponse(responseCode = "400", description = "확인 비밀번호가 다릅니다."),
		@ApiResponse(responseCode = "200", description = "비밀번호 변경이 완료 되었습니다.")
	})
	@PatchMapping("/change/password")
	public ResponseHandler<Boolean> changePassword(@RequestBody @Valid MemberPasswordChangeCommand request, @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
		return ResponseHandler.<Boolean>builder()
			.data(memberService.changePassword(request, customMemberDetails.getMemberId()))
			.message("비밀번호 변경이 완료되었습니다.")
			.build();
	}

	@Operation(summary = "프로필 사진이 등록되었습니다.", responses = {
		@ApiResponse(responseCode = "404", description = "등록된 회원이 아닙니다."),
		@ApiResponse(responseCode = "500", description = "파일 업로드중 에러가 발생하였습니다."),
		@ApiResponse(responseCode = "200", description = "프로필 사진이 등록되었습니다.")
	})
	@PatchMapping("/change/profile")
	public ResponseHandler<Boolean> changeProfile(@RequestParam MultipartFile file, @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
		return ResponseHandler.<Boolean>builder()
			.data(memberService.changeProfile(file, customMemberDetails.getMemberId()))
			.message("프로필 사진이 등록되었습니다.")
			.build();
	}

	@Operation(summary = "이름이 변경되었습니다.", responses = {
		@ApiResponse(responseCode = "404", description = "등록된 회원이 아닙니다."),
		@ApiResponse(responseCode = "200", description = "이름이 변경되었습니다.")
	})
	@PatchMapping("/change/name")
	public ResponseHandler<Boolean> changeName(@Parameter(description = "변경할 닉네임") @RequestParam String name, @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
		return ResponseHandler.<Boolean>builder()
			.data(memberService.changeName(name, customMemberDetails.getMemberId()))
			.message("이름이 변경되었습니다.")
			.build();
	}

	@Operation(summary = "알림 상태가 변경되었습니다.", responses = {
		@ApiResponse(responseCode = "404", description = "등록된 회원이 아닙니다."),
		@ApiResponse(responseCode = "200", description = "알림 상태가 변경되었습니다.")
	})
	@PatchMapping("/change/alarm")
	public ResponseHandler<Boolean> changeAlarm(@Parameter(description = "변경할 알림 상태") @RequestParam AlarmStatus alarmStatus, @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
		return ResponseHandler.<Boolean>builder()
			.data(memberService.changeAlarm(alarmStatus, customMemberDetails.getMemberId()))
			.message("알림 상태가 변경되었습니다.")
			.build();
	}

	@Operation(summary = "수업 기록 여부가 변경되었습니다.", responses = {
		@ApiResponse(responseCode = "404", description = "등록된 회원이 아닙니다."),
		@ApiResponse(responseCode = "200", description = "수업 기록 여부가 변경되었습니다.")
	})
	@PatchMapping("/change/trainer-feedback")
	public ResponseHandler<Boolean> changeTrainerFeedback(@Parameter(description = "변경할 수업 기록 상태") @RequestParam TrainerFeedback trainerFeedback, @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
		return ResponseHandler.<Boolean>builder()
			.data(memberService.changeTrainerFeedback(trainerFeedback, customMemberDetails.getMemberId()))
			.message("수업 기록 여부가 변경되었습니다.")
			.build();
	}

	@PostMapping("/access-token/naver")
	public ResponseHandler<Tokens> getNaverAccessToken(@Parameter(description = "소셜에서 발급한 code") @RequestParam String code,
													   @Parameter(description = "소셜에서 발급한 state") @RequestParam String state) {
		return ResponseHandler.<Tokens>builder()
				.data(memberService.getNaverAccessToken(code, state))
				.message("요청이 처리되었습니다.")
				.build();
	}

	@PostMapping("/access-token/kakao")
	public ResponseHandler<Tokens> getKakaoAccessToken(@Parameter(description = "소셜에서 발급한 code") @RequestParam String code) {
		return ResponseHandler.<Tokens>builder()
				.data(memberService.getKakaoAccessToken(code))
				.message("요청이 처리되었습니다.")
				.build();
	}
}