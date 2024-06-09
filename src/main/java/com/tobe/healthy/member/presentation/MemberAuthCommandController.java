package com.tobe.healthy.member.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.member.application.MemberAuthCommandService;
import com.tobe.healthy.member.domain.dto.in.*;
import com.tobe.healthy.member.domain.dto.out.CommandFindMemberPasswordResult;
import com.tobe.healthy.member.domain.dto.out.CommandJoinMemberResult;
import com.tobe.healthy.member.domain.entity.Tokens;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/v1")
@Slf4j
@Tag(name = "01. 회원 인증 API", description = "인증/권한 없이 접근할 수 있는 회원 API")
public class MemberAuthCommandController {

	private final MemberAuthCommandService memberAuthCommandService;

	@Operation(summary = "이메일로 인증번호를 전송한다.", description = "이메일로 6자리의 난수를 만들어 3분간 유효한 인증번호를 전송한다.",
		responses = {
			@ApiResponse(responseCode = "500", description = "메일 전송중 에러가 발생하였습니다."),
			@ApiResponse(responseCode = "400", description = "이미 등록된 이메일입니다."),
			@ApiResponse(responseCode = "200", description = "이메일로 인증번호를 전송하였습니다.")
	})
	@PostMapping("/validation/send-email")
	public ResponseHandler<String> sendEmailVerification(@RequestBody @Valid CommandValidateEmail request) {
		return ResponseHandler.<String>builder()
			.data(memberAuthCommandService.sendEmailVerification(request))
			.message("이메일로 인증번호를 발송중이에요!")
			.build();
	}

	@Operation(summary = "이메일 인증번호를 검증한다.", description = "이메일로 전송된 인증번호를 3분안에 입력해야 검증에 성공한다.",
		responses = {
			@ApiResponse(responseCode = "400", description = "이메일 인증번호가 일치하지 않습니다."),
			@ApiResponse(responseCode = "200", description = "이메일 인증번호가 일치합니다.")
	})
	@PostMapping("/validation/confirm-email")
	public ResponseHandler<Boolean> verifyAuthMail(@RequestBody @Valid CommandVerification reuqest) {
		return ResponseHandler.<Boolean>builder()
			.data(memberAuthCommandService.verifyEmailAuthNumber(reuqest))
			.message("인증번호가 확인되었습니다.")
			.build();
	}

	@Operation(summary = "회원가입", description = "이름, 비밀번호 규칙, 아이디, 이메일 중복을 검증하고 비밀번호는 암호화해서 가입시킨다.",
		responses = {
			@ApiResponse(responseCode = "400(1)", description = "이름의 길이는 2자 이상이여야 합니다."),
			@ApiResponse(responseCode = "400(2)", description = "이름은 한글 또는 영어만 입력할 수 있습니다."),
			@ApiResponse(responseCode = "400(3)", description = "확인 비밀번호가 일치하지 않습니다."),
			@ApiResponse(responseCode = "400(4)", description = "비밀번호는 영어 대/소문자와 숫자로 구성된 8자리 이상 문자여야 합니다."),
			@ApiResponse(responseCode = "400(5)", description = "아이디에 한글을 포함할 수 없습니다."),
			@ApiResponse(responseCode = "400(6)", description = "이미 등록된 아이디입니다."),
			@ApiResponse(responseCode = "400(7)", description = "이미 등록된 이메일입니다."),
			@ApiResponse(responseCode = "200", description = "회원가입에 성공하였습니다.")
	})
	@PostMapping("/join")
	public ResponseHandler<CommandJoinMemberResult> join(@RequestBody @Valid CommandJoinMember request) {
		return ResponseHandler.<CommandJoinMemberResult>builder()
			.data(memberAuthCommandService.joinMember(request))
			.message("회원가입이 완료되었습니다.")
			.build();
	}

	@Operation(summary = "로그인", description = "로그인에 성공하면, Access/Refresh token, userId, memberType, gymId를 반환한다.",
		responses = {
			@ApiResponse(responseCode = "400", description = "아이디 또는 비밀번호가 잘못되었습니다.",
				content = {@Content(schema = @Schema(implementation = Tokens.class))}),
			@ApiResponse(responseCode = "200", description = "로그인에 성공하고, Access Token, Refresh Token, userId, Role을 반환한다.")
	})
	@PostMapping("/login")
	public ResponseHandler<Tokens> login(@RequestBody @Valid CommandLoginMember request) {
		return ResponseHandler.<Tokens>builder()
			.data(memberAuthCommandService.login(request))
			.message("로그인 되었습니다.")
			.build();
	}

	@Operation(summary = "토큰을 갱신한다.", description = "refresh token이 유효하면 AccessToken을 생성하여 반환한다.",
		responses = {
			@ApiResponse(responseCode = "404(1)", description = "Refresh Token을 찾을 수 없습니다."),
			@ApiResponse(responseCode = "404(2)", description = "회원을 찾을 수 없습니다."),
			@ApiResponse(responseCode = "400", description = "Refresh Token이 유효하지 않습니다."),
			@ApiResponse(responseCode = "200", description = "Access Token, Refresh Token, userId, Role을 반환한다.")
	})
	@PostMapping("/refresh-token")
	public ResponseHandler<Tokens> refreshToken(@RequestBody @Valid CommandRefreshToken request) {
		return ResponseHandler.<Tokens>builder()
			.data(memberAuthCommandService.refreshToken(request))
			.message("토큰이 갱신되었습니다.")
			.build();
	}

	@Operation(summary = "비밀번호 찾기", description = "등록된 이메일로 초기화 비밀번호를 전송한다.",
		responses = {
			@ApiResponse(responseCode = "500", description = "메일 전송중 에러가 발생했습니다."),
			@ApiResponse(responseCode = "404", description = "등록된 회원이 아닙니다."),
			@ApiResponse(responseCode = "200", description = "등록된 이메일로 초기화 비밀번호를 전송한다.")
	})
	@PostMapping("/find/password")
	public ResponseHandler<CommandFindMemberPasswordResult> findMemberPW(@RequestBody @Valid CommandFindMemberPassword request) {

		CommandFindMemberPasswordResult findMemberPasswordResult = memberAuthCommandService.findMemberPW(request);

		return ResponseHandler.<CommandFindMemberPasswordResult>builder()
			.data(findMemberPasswordResult)
			.message(findMemberPasswordResult.getMessage())
			.build();
	}

	@Operation(summary = "네이버 소셜 로그인", description = "인가코드로 네이버에서 정보를 받아온 뒤에, 로그인 프로세스를 거친다. 비회원인 경우 회원가입 프로세스를 추가로 거친다.",
		responses = {
			@ApiResponse(responseCode = "500(1)", description = "네이버 소셜서버와 연동중 에러가 발생하였습니다."),
			@ApiResponse(responseCode = "500(2)", description = "파일 업로드중 에러가 발생하였습니다."),
			@ApiResponse(responseCode = "500(3)", description = "소셜 프로필을 가져오던 중 에러가 발생하였습니다."),
			@ApiResponse(responseCode = "200", description = "요청 처리에 성공하였습니다.")
	})
	@PostMapping("/access-token/naver")
	public ResponseHandler<Tokens> getNaverAccessToken(@RequestBody CommandSocialLogin request) {
		return ResponseHandler.<Tokens>builder()
			.data(memberAuthCommandService.getNaverAccessToken(request))
			.message("요청이 처리되었습니다.")
			.build();
	}

	@Operation(summary = "카카오 소셜 로그인", description = "인가코드로 카카오에서 정보를 받아온 뒤에, 로그인 프로세스를 거친다. 비회원인 경우 회원가입 프로세스를 추가로 거친다.",
		responses = {
			@ApiResponse(responseCode = "500(1)", description = "카카오 소셜서버와 연동중 에러가 발생하였습니다."),
			@ApiResponse(responseCode = "500(2)", description = "JSON 토큰을 파싱중 에러가 발생하였습니다."),
			@ApiResponse(responseCode = "500(3)", description = "파일 업로드중 에러가 발생하였습니다."),
			@ApiResponse(responseCode = "500(4)", description = "소셜 프로필을 가져오던 중 에러가 발생하였습니다."),
			@ApiResponse(responseCode = "200", description = "요청 처리에 성공하였습니다.")
	})
	@PostMapping("/access-token/kakao")
	public ResponseHandler<Tokens> getKakaoAccessToken(@RequestBody CommandSocialLogin request) {
		return ResponseHandler.<Tokens>builder()
			.data(memberAuthCommandService.getKakaoAccessToken(request))
			.message("요청이 처리되었습니다.")
			.build();
	}

	@Operation(summary = "구글 소셜 로그인", description = "인가코드로 구글에서 정보를 받아온 뒤에, 로그인 프로세스를 거친다. 비회원인 경우 회원가입 프로세스를 추가로 거친다.",
			responses = {
					@ApiResponse(responseCode = "500", description = "구글 소셜서버와 연동중 에러가 발생하였습니다."),
					@ApiResponse(responseCode = "200", description = "요청 처리에 성공하였습니다.")
	})
	@PostMapping("/access-token/google")
	public ResponseHandler<Tokens> getGoogleOAuth(@RequestBody CommandSocialLogin command) {
		return ResponseHandler.<Tokens>builder()
			.data(memberAuthCommandService.getGoogleOAuth(command))
			.message("요청이 처리되었습니다.")
			.build();
	}
}