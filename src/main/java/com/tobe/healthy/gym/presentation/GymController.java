package com.tobe.healthy.gym.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.gym.application.GymService;
import com.tobe.healthy.gym.domain.dto.GymListCommandResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gym/v1")
@Slf4j
@Valid
@Tag(name = "04.헬스장 API", description = "헬스장 조회 API")
public class GymController {

	private final GymService gymService;

	@Operation(summary = "헬스장을 등록한다.", responses = {
			@ApiResponse(responseCode = "200", description = "헬스장을 등록하였습니다.")
	})
	@PostMapping("/gym")
	public ResponseHandler<Boolean> registerGym(@Parameter(description = "헬스장 이름") @RequestParam String name) {
		return ResponseHandler.<Boolean>builder()
				.data(gymService.registerGym(name))
				.message("헬스장을 등록하였습니다.")
				.build();
	}

	@Operation(summary = "모든 헬스장을 조회한다.", responses = {
			@ApiResponse(responseCode = "200", description = "모든 헬스장을 조회한다.")
	})
	@GetMapping("/gym")
	public ResponseHandler<List<GymListCommandResult>> findAllGym() {
		return ResponseHandler.<List<GymListCommandResult>>builder()
				.data(gymService.findAllGym())
				.message("모든 헬스장을 조회하였습니다.")
				.build();
	}

	@Operation(summary = "내 헬스장으로 등록한다.", responses = {
			@ApiResponse(responseCode = "404", description = "회원을 찾을 수 없습니다."),
			@ApiResponse(responseCode = "404", description = "헬스장을 찾을 수 없습니다."),
			@ApiResponse(responseCode = "200", description = "내 헬스장으로 등록하였습니다.")
	})
	@PatchMapping("/gym/{gymId}")
	public ResponseHandler<Boolean> selectMyGym(@Parameter(description = "헬스장 ID") @PathVariable(name = "gymId") Long gymId,
												@AuthenticationPrincipal CustomMemberDetails member) {
		return ResponseHandler.<Boolean>builder()
				.data(gymService.selectMyGym(gymId, member.getMemberId()))
				.message("내 헬스장으로 등록되었습니다.")
				.build();
	}
}