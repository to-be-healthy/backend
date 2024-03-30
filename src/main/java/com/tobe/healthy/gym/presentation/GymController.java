package com.tobe.healthy.gym.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.gym.application.GymService;
import com.tobe.healthy.gym.domain.dto.GymListCommandResult;
import com.tobe.healthy.gym.domain.dto.MemberInTeamCommandResult;
import com.tobe.healthy.gym.domain.dto.TrainerCommandResult;
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
@RequestMapping("/gyms/v1")
@Slf4j
@Valid
@Tag(name = "04.헬스장 API", description = "헬스장 조회 API")
public class GymController {

	private final GymService gymService;

	@Operation(summary = "헬스장을 등록한다.", description = "트레이너 또는 관리자가 헬스장을 등록한다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "헬스장을 등록하였습니다.")
	})
	@PostMapping
	public ResponseHandler<Boolean> registerGym(@Parameter(description = "헬스장 이름") @RequestParam String name) {
		return ResponseHandler.<Boolean>builder()
				.data(gymService.registerGym(name))
				.message("헬스장을 등록하였습니다.")
				.build();
	}

	@Operation(summary = "모든 헬스장을 조회한다.", description = "등록된 모든 헬스장을 조회한다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "모든 헬스장을 조회한다.")
	})
	@GetMapping
	public ResponseHandler<List<GymListCommandResult>> findAllGym() {
		return ResponseHandler.<List<GymListCommandResult>>builder()
				.data(gymService.findAllGym())
				.message("모든 헬스장을 조회하였습니다.")
				.build();
	}

	@Operation(summary = "내 헬스장으로 등록한다.", description = "회원/트레이너가 내가 다니는 헬스장으로 등록한다.",
		responses = {
			@ApiResponse(responseCode = "404", description = "회원을 찾을 수 없습니다."),
			@ApiResponse(responseCode = "404", description = "헬스장을 찾을 수 없습니다."),
			@ApiResponse(responseCode = "200", description = "내 헬스장으로 등록하였습니다.")
	})
	@PatchMapping("/{gymId}")
	public ResponseHandler<Boolean> selectMyGym(@Parameter(description = "헬스장 ID") @PathVariable(name = "gymId") Long gymId,
												@AuthenticationPrincipal CustomMemberDetails member) {
		return ResponseHandler.<Boolean>builder()
				.data(gymService.selectMyGym(gymId, member.getMemberId()))
				.message("내 헬스장으로 등록되었습니다.")
				.build();
	}

	@Operation(summary = "회원이 내 트레이너로 등록한다.", description = "회원이 내 트레이너로 등록한다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "내 트레이너로 등록하였습니다.")
	})
	@PostMapping("/{gymId}/trainer/{trainerId}")
	public ResponseHandler<Boolean> selectMyTrainer(@Parameter(description = "헬스장 ID") @PathVariable(name = "gymId") Long gymId,
													@Parameter(description = "트레이너 ID") @PathVariable(name = "trainerId") Long trainerId,
													@AuthenticationPrincipal CustomMemberDetails member) {
		return ResponseHandler.<Boolean>builder()
			.data(gymService.selectMyTrainer(gymId, trainerId, member.getMemberId()))
			.message("내 트레이너로 등록되었습니다.")
			.build();
	}

	@Operation(summary = "헬스장의 모든 트레이너들을 조회한다.", description = "헬스장의 모든 트레이너들을 조회한다.(새로운 트레이너가 상단에 있도록)",
		responses = {
			@ApiResponse(responseCode = "200", description = "헬스장에 모든 트레이너 조회완료")
	})
	@GetMapping("/{gymId}/trainers")
	public ResponseHandler<List<TrainerCommandResult>> findAllTrainersByGym(@Parameter(description = "헬스장 ID") @PathVariable(name = "gymId") Long gymId) {
		return ResponseHandler.<List<TrainerCommandResult>>builder()
				.data(gymService.findAllTrainersByGym(gymId))
				.message("헬스장의 모든 트레이너들을 조회하였습니다.")
				.build();
	}

	@Operation(summary = "트레이너가 관리하는 회원들을 조회한다.", description = "트레이너가 관리하는 회원 전체를 조회한다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "트레이너가 관리하는 회원 조회 완료")
	})
	@GetMapping("/members")
	public ResponseHandler<List<MemberInTeamCommandResult>> findAllMyMemberInTeam(@AuthenticationPrincipal CustomMemberDetails member) {
		return ResponseHandler.<List<MemberInTeamCommandResult>>builder()
			.data(gymService.findAllMyMemberInTeam(member.getMemberId()))
			.message("트레이너가 관리하는 회원을 조회하였습니다.")
			.build();
	}
}