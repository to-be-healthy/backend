package com.tobe.healthy.member.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.member.application.MemberAuthService;
import com.tobe.healthy.member.domain.dto.in.CommandValidateEmail;
import com.tobe.healthy.member.domain.dto.in.RetrieveMemberId;
import com.tobe.healthy.member.domain.dto.in.RetrieveMemberId.FindMemberIdResult;
import com.tobe.healthy.member.domain.dto.out.InvitationMappingResult;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "01. 회원 인증 API", description = "인증/권한 없이 접근할 수 있는 회원 API")
public class MemberAuthController {

	private final MemberAuthService memberAuthService;

	@Operation(summary = "아이디 중복 확인하기",
		responses = {
			@ApiResponse(responseCode = "400", description = "이미 등록된 아이디입니다."),
			@ApiResponse(responseCode = "200", description = "사용 가능한 아이디입니다.")
	})
	@GetMapping("/validation/user-id")
	public ResponseHandler<Boolean> validateUsernameDuplication(@RequestParam(name = "userId") String userId) {
		return ResponseHandler.<Boolean>builder()
			.data(memberAuthService.validateUserIdDuplication(userId))
			.message("사용할 수 있는 아이디입니다.")
			.build();
	}

	@Operation(summary = "이메일 중복을 확인한다.",
		responses = {
			@ApiResponse(responseCode = "400", description = "이미 등록된 이메일입니다."),
			@ApiResponse(responseCode = "200", description = "사용 가능한 이메일입니다.")
	})
	@GetMapping("/validation/email")
	public ResponseHandler<Boolean> validateEmailDuplication(@RequestParam @Valid CommandValidateEmail request) {
		return ResponseHandler.<Boolean>builder()
			.data(memberAuthService.validateEmailDuplication(request))
			.message("사용 가능한 이메일입니다.")
			.build();
	}

	@Operation(summary = "아이디를 찾는다.", description = "이메일과 이름을 기준으로 일치하는 아이디를 찾는다.(소셜은 찾을 수 없음)",
		responses = {
			@ApiResponse(responseCode = "404", description = "등록된 회원이 아닙니다."),
			@ApiResponse(responseCode = "200", description = "이메일 이름이 일치한 사용자 아이디를 반환한다.")
	})
	@PostMapping("/find/user-id")
	public ResponseHandler<FindMemberIdResult> findUserId(@RequestBody @Valid RetrieveMemberId request) {
		return ResponseHandler.<FindMemberIdResult>builder()
			.data(memberAuthService.findUserId(request))
			.message("아이디 찾기에 성공하였습니다.")
			.build();
	}

	@Operation(summary = "초대링크 uuid 데이터 조회", responses = {
		@ApiResponse(responseCode = "404", description = "초대링크를 찾을 수 없습니다."),
		@ApiResponse(responseCode = "200", description = "성공")
	})
	@GetMapping("/invitation/uuid")
	public ResponseHandler<InvitationMappingResult> getInvitationMapping(@RequestParam String uuid) {
		return ResponseHandler.<InvitationMappingResult>builder()
			.data(memberAuthService.getInvitationMapping(uuid))
			.message("조회가 완료되었습니다.")
			.build();
	}
}
