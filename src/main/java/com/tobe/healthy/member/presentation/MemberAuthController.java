package com.tobe.healthy.member.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.member.application.MemberService;
import com.tobe.healthy.member.domain.dto.in.*;
import com.tobe.healthy.member.domain.dto.in.MemberFindIdCommand.MemberFindIdCommandResult;
import com.tobe.healthy.member.domain.dto.out.InvitationMappingResult;
import com.tobe.healthy.member.domain.dto.out.MemberJoinCommandResult;
import com.tobe.healthy.member.domain.entity.Tokens;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/v1")
@Slf4j
@Valid
@Tag(name = "01. 사용자 인증 API", description = "인증/권한 없이 접근할 수 있는 사용자 API")
public class MemberAuthController {

	private final MemberService memberService;

	@Operation(summary = "아이디 중복 확인하기",
		responses = {
			@ApiResponse(responseCode = "400", description = "이미 등록된 아이디입니다."),
			@ApiResponse(responseCode = "200", description = "사용 가능한 아이디입니다.")
	})
	@GetMapping("/validation/user-id")
	public ResponseHandler<Boolean> validateUsernameDuplication(@Parameter(description = "아이디") @RequestParam(name = "userId") String userId) {
		return ResponseHandler.<Boolean>builder()
			.data(memberService.validateUserIdDuplication(userId))
			.message("사용 가능한 아이디입니다.")
			.build();
	}

	@Operation(summary = "이메일 중복을 확인한다.",
		responses = {
			@ApiResponse(responseCode = "400", description = "이미 등록된 이메일입니다."),
			@ApiResponse(responseCode = "200", description = "사용 가능한 이메일입니다.")
	})
	@GetMapping("/validation/email")
	public ResponseHandler<Boolean> validateEmailDuplication(@Parameter(description = "이메일") @RequestParam String email) {
		return ResponseHandler.<Boolean>builder()
			.data(memberService.validateEmailDuplication(email))
			.message("사용 가능한 이메일입니다.")
			.build();
	}

	@Operation(summary = "이메일로 인증번호를 전송한다.", description = "이메일로 6자리의 난수를 만들어 3분간 유효한 인증번호를 전송한다.",
		responses = {
			@ApiResponse(responseCode = "500", description = "메일 전송중 에러가 발생하였습니다."),
			@ApiResponse(responseCode = "400", description = "이미 등록된 이메일입니다."),
			@ApiResponse(responseCode = "200", description = "이메일로 인증번호를 전송하였습니다.")
	})
	@PostMapping("/validation/send-email")
	public ResponseHandler<String> sendEmailVerification(@Parameter(description = "이메일") @RequestParam String email) {
		return ResponseHandler.<String>builder()
			.data(memberService.sendEmailVerification(email))
			.message("이메일 인증번호가 전송되었습니다.")
			.build();
	}

	@Operation(summary = "이메일 인증번호를 검증한다.", description = "이메일로 전송된 인증번호를 3분안에 입력해야 검증에 성공한다.",
		responses = {
			@ApiResponse(responseCode = "400", description = "이메일 인증번호가 일치하지 않습니다."),
			@ApiResponse(responseCode = "200", description = "이메일 인증번호가 일치합니다.")
	})
	@PostMapping("/validation/confirm-email")
	public ResponseHandler<Boolean> verifyAuthMail(@Parameter(description = "이메일") @RequestParam String email,
												   @Parameter(description = "이메일 인증번호") @RequestParam String emailKey) {
		return ResponseHandler.<Boolean>builder()
			.data(memberService.verifyEmailAuthNumber(emailKey, email))
			.message("인증번호가 확인되었습니다.")
			.build();
	}

	@Operation(summary = "회원가입", description = "이름, 비밀번호 규칙, 아이디, 이메일 중복을 검증하고 비밀번호는 암호화해서 가입시킨다.",
		responses = {
			@ApiResponse(responseCode = "400", description = "이름의 길이는 2자 이상이여야 합니다."),
			@ApiResponse(responseCode = "400", description = "이름은 한글 또는 영어만 입력할 수 있습니다."),
			@ApiResponse(responseCode = "400", description = "확인 비밀번호가 일치하지 않습니다."),
			@ApiResponse(responseCode = "400", description = "비밀번호는 영어 대/소문자와 숫자로 구성된 8자리 이상 문자여야 합니다."),
			@ApiResponse(responseCode = "400", description = "아이디에 한글을 포함할 수 없습니다."),
			@ApiResponse(responseCode = "400", description = "이미 등록된 아이디입니다."),
			@ApiResponse(responseCode = "400", description = "이미 등록된 이메일입니다."),
			@ApiResponse(responseCode = "200", description = "회원가입에 성공하였습니다.")
	})
	@PostMapping("/join")
	public ResponseHandler<MemberJoinCommandResult> join(@RequestBody MemberJoinCommand request) {
		return ResponseHandler.<MemberJoinCommandResult>builder()
			.data(memberService.joinMember(request))
			.message("회원가입이 완료되었습니다.")
			.build();
	}

	@Operation(summary = "로그인", description = "로그인에 성공하면, Access/Refresh token, userId, memberType, gymId를 반환한다.",
		responses = {
			@ApiResponse(responseCode = "400", description = "아이디 또는 비밀번호가 잘못되었습니다."),
			@ApiResponse(responseCode = "200", description = "로그인에 성공하고, Access Token, Refresh Token, userId, Role을 반환한다.")
	})
	@PostMapping("/login")
	public ResponseHandler<Tokens> login(@RequestBody MemberLoginCommand request) {
		return ResponseHandler.<Tokens>builder()
			.data(memberService.login(request))
			.message("로그인 되었습니다.")
			.build();
	}

	@Operation(summary = "토큰을 갱신한다.", description = "refresh token이 유효하면 AccessToken을 생성하여 반환한다.",
		responses = {
			@ApiResponse(responseCode = "404", description = "Refresh Token을 찾을 수 없습니다."),
			@ApiResponse(responseCode = "400", description = "Refresh Token이 유효하지 않습니다."),
			@ApiResponse(responseCode = "404", description = "회원을 찾을 수 없습니다."),
			@ApiResponse(responseCode = "200", description = "Access Token, Refresh Token, userId, Role을 반환한다.")
	})
	@PostMapping("/refresh-token")
	public ResponseHandler<Tokens> refreshToken(@Parameter(description = "아이디") @RequestParam String userId,
												@Parameter(description = "갱신 토큰") @RequestParam String refreshToken) {
		return ResponseHandler.<Tokens>builder()
			.data(memberService.refreshToken(userId, refreshToken))
			.message("토큰이 갱신되었습니다.")
			.build();
	}

	@Operation(summary = "아이디를 찾는다.", description = "이메일과 이름을 기준으로 일치하는 아이디를 찾는다.(소셜은 찾을 수 없음)",
		responses = {
			@ApiResponse(responseCode = "404", description = "등록된 회원이 아닙니다."),
			@ApiResponse(responseCode = "200", description = "이메일 이름이 일치한 사용자 아이디를 반환한다.")
	})
	@PostMapping("/find/user-id")
	public ResponseHandler<MemberFindIdCommandResult> findUserId(@RequestBody MemberFindIdCommand request) {
		return ResponseHandler.<MemberFindIdCommandResult>builder()
			.data(memberService.findUserId(request))
			.message("아이디 찾기에 성공하였습니다.")
			.build();
	}

	@Operation(summary = "비밀번호 찾기", description = "등록된 이메일로 초기화 비밀번호를 전송한다.",
		responses = {
			@ApiResponse(responseCode = "500", description = "메일 전송중 에러가 발생했습니다."),
			@ApiResponse(responseCode = "404", description = "등록된 회원이 아닙니다."),
			@ApiResponse(responseCode = "200", description = "등록된 이메일로 초기화 비밀번호를 전송한다.")
	})
	@PostMapping("/find/password")
	public ResponseHandler<String> findMemberPW(@RequestBody MemberFindPWCommand request) {
		return ResponseHandler.<String>builder()
			.data(memberService.findMemberPW(request))
			.message("이메일에 초기화 비밀번호가 전송되었습니다.")
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

	@Operation(summary = "네이버 소셜 로그인", description = "인가코드로 네이버에서 정보를 받아온 뒤에, 로그인 프로세스를 거친다. 비회원인 경우 회원가입 프로세스를 추가로 거친다.",
		responses = {
			@ApiResponse(responseCode = "500", description = "네이버 소셜서버와 연동중 에러가 발생하였습니다."),
			@ApiResponse(responseCode = "500", description = "파일 업로드중 에러가 발생하였습니다."),
			@ApiResponse(responseCode = "500", description = "소셜 프로필을 가져오던 중 에러가 발생하였습니다."),
			@ApiResponse(responseCode = "200", description = "요청 처리에 성공하였습니다.")
	})
	@PostMapping("/access-token/naver")
	public ResponseHandler<Tokens> getNaverAccessToken(@RequestBody SocialLoginCommand request) {
		return ResponseHandler.<Tokens>builder()
			.data(memberService.getNaverAccessToken(request))
			.message("요청이 처리되었습니다.")
			.build();
	}

	@Operation(summary = "카카오 소셜 로그인", description = "인가코드로 카카오에서 정보를 받아온 뒤에, 로그인 프로세스를 거친다. 비회원인 경우 회원가입 프로세스를 추가로 거친다.",
		responses = {
			@ApiResponse(responseCode = "500", description = "카카오 소셜서버와 연동중 에러가 발생하였습니다."),
			@ApiResponse(responseCode = "500", description = "JSON 토큰을 파싱중 에러가 발생하였습니다."),
			@ApiResponse(responseCode = "500", description = "파일 업로드중 에러가 발생하였습니다."),
			@ApiResponse(responseCode = "500", description = "소셜 프로필을 가져오던 중 에러가 발생하였습니다."),
			@ApiResponse(responseCode = "200", description = "요청 처리에 성공하였습니다.")
	})
	@PostMapping("/access-token/kakao")
	public ResponseHandler<Tokens> getKakaoAccessToken(@RequestBody SocialLoginCommand request) {
		return ResponseHandler.<Tokens>builder()
			.data(memberService.getKakaoAccessToken(request))
			.message("요청이 처리되었습니다.")
			.build();
	}

	@PostMapping("/access-token/google")
	public ResponseHandler<Tokens> getGoogleOAuth(@RequestBody SocialLoginCommand command) {
		return ResponseHandler.<Tokens>builder()
			.data(memberService.getGoogleOAuth(command))
			.message("요청이 처리되었습니다.")
			.build();
	}
}