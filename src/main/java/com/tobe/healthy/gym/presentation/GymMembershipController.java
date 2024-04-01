package com.tobe.healthy.gym.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.gym.application.GymMembershipService;
import com.tobe.healthy.gym.domain.dto.in.MembershipAddCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/membership/v1")
@Slf4j
@Valid
@Tag(name = "04-01.헬스장 회원권 API", description = "헬스장 회원권 API")
public class GymMembershipController {

	private final GymMembershipService gymMembershipService;

	@Operation(summary = "트레이너가 학생의 헬스장 이용권(시작날짜~종료날짜), PT 수업 횟수를 등록한다.",
			description = "트레이너가 학생의 헬스장 이용권(시작날짜~종료날짜), PT 수업 횟수를 등록한다.",
			responses = {
					@ApiResponse(responseCode = "200", description = "헬스장 이용권, 수업권을 등록하였습니다.")
			})
	@PostMapping
	public ResponseHandler<Void> registerGymMembership(@RequestBody MembershipAddCommand command) {
		gymMembershipService.registerGymMembership(command);
		return ResponseHandler.<Void>builder()
				.message("헬스장 이용권, 수업권을 등록하였습니다.")
				.build();
	}

}