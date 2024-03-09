package com.tobe.healthy.member.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.member.application.MemberService;
import com.tobe.healthy.member.domain.dto.in.MemberFindIdCommandRequest;
import com.tobe.healthy.member.domain.dto.in.MemberFindPWCommand;
import com.tobe.healthy.member.domain.dto.in.MemberJoinCommand;
import com.tobe.healthy.member.domain.dto.in.MemberLoginCommand;
import com.tobe.healthy.member.domain.dto.in.MemberOauthCommandRequest;
import com.tobe.healthy.member.domain.dto.in.VerifyAuthMailRequest;
import com.tobe.healthy.member.domain.dto.out.MemberJoinCommandResult;
import com.tobe.healthy.member.domain.entity.Tokens;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
@Tag(name = "member", description = "회원 관련 API")
public class MemberController {

	private final MemberService memberService;

	@Operation(summary = "이메일에 인증번호를 전송한다.", responses = {
		@ApiResponse(responseCode = "401", description = "이미 등록된 이메일"),
		@ApiResponse(responseCode = "504", description = "메일 전송중 에러발생"),
		@ApiResponse(responseCode = "200", description = "이메일 인증번호 전송 성공")
	})
	@GetMapping("/send-auth")
	public ResponseHandler<String> sendAuthMail(@RequestParam String email) {
		return ResponseHandler.<String>builder()
			.statusCode(HttpStatus.OK)
			.data(memberService.sendAuthMail(email))
			.message("인증번호를 전송하였습니다.")
			.build();
	}

	@Operation(summary = "인증번호를 검증한다.", responses = {
		@ApiResponse(responseCode = "410", description = "잘못된 인증번호 입력"),
		@ApiResponse(responseCode = "200", description = "이메일 인증번호 검증 성공")
	})
	@PostMapping("/verify-auth")
	public ResponseHandler<String> verifyAuthMail(@RequestBody VerifyAuthMailRequest request) {
		return ResponseHandler.<String>builder()
			.statusCode(HttpStatus.OK)
			.data(memberService.verifyAuthMail(request))
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
			.statusCode(HttpStatus.OK)
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
			.statusCode(HttpStatus.OK)
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
	public ResponseHandler<Tokens> refreshToken(String email, String refreshToken) {
		return ResponseHandler.<Tokens>builder()
			.statusCode(HttpStatus.OK)
			.data(memberService.refreshToken(email, refreshToken))
			.message("토큰이 갱신되었습니다.")
			.build();
	}

	@Operation(summary = "아이디를 찾는다.", responses = {
		@ApiResponse(responseCode = "400", description = "등록된 회원이 아닙니다."),
		@ApiResponse(responseCode = "200", description = "휴대폰 번호, 닉네임에 일치하는 이메일을 반환한다.")
	})
	@PostMapping("/find-id")
	public ResponseHandler<String> findMemberId(@RequestBody @Valid MemberFindIdCommandRequest request) {
		return ResponseHandler.<String>builder()
			.statusCode(HttpStatus.OK)
			.data(memberService.findMemberId(request))
			.message("아이디 찾기에 성공하였습니다.")
			.build();
	}

	@Operation(summary = "비밀번호를 찾는다.", responses = {
		@ApiResponse(responseCode = "400", description = "등록된 회원이 아닙니다."),
		@ApiResponse(responseCode = "200", description = "등록된 이메일에 초기화 비밀번호를 전송한다.")
	})
	@PostMapping("/find-pw")
	public ResponseHandler<String> findMemberPW(@RequestBody @Valid MemberFindPWCommand request) {
		return ResponseHandler.<String>builder()
			.statusCode(HttpStatus.OK)
			.data(memberService.findMemberPW(request))
			.message("이메일에 초기화 비밀번호가 전송되었습니다.")
			.build();
	}

	@GetMapping("/code/kakao")
	public ResponseEntity<?> oauth(MemberOauthCommandRequest request) {
		return ResponseEntity.ok(memberService.getAccessToken(request.getCode()));
	}

	@PostMapping("/withdraw")
	public ResponseHandler<String> withdrawMember(String email) {
		return ResponseHandler.<String>builder()
			.statusCode(HttpStatus.OK)
			.data(memberService.withdrawMember(email))
			.message("회원탈퇴 되었습니다.")
			.build();
	}
}