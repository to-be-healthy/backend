package com.tobe.healthy.trainer.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.trainer.application.TrainerService;
import com.tobe.healthy.trainer.domain.dto.TrainerMemberMappingDto;
import com.tobe.healthy.trainer.domain.dto.in.MemberInviteCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Tag(name = "05. 트레이너 API", description = "트레이너 API")
@Slf4j
public class TrainerController {

    private final TrainerService trainerService;

    @Operation(summary = "회원 초대하기.", responses = {
		@ApiResponse(responseCode = "400", description = "등록된 회원이 아닙니다."),
		@ApiResponse(responseCode = "200", description = "회원초대가 완료 되었습니다.")
    })
    @PostMapping("/invitation")
    public ResponseHandler<Void> inviteMember(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
                                           @Parameter(description = "이메일") @RequestBody MemberInviteCommand command) {
        trainerService.inviteMember(command, customMemberDetails.getMember());
        return ResponseHandler.<Void>builder()
                .message("회원초대가 완료 되었습니다.")
                .build();
    }

    @Operation(summary = "내 회원으로 등록하기", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "매핑ID, 트레이너ID, 회원ID를 반환한다.")
    })
    @PostMapping("/trainers/{trainerId}/members/{memberId}")
    public ResponseHandler<TrainerMemberMappingDto> addMemberOfTrainer(@PathVariable("trainerId") Long trainerId,
                                                                       @PathVariable("memberId") Long memberId) {
        return ResponseHandler.<TrainerMemberMappingDto>builder()
                .data(trainerService.addMemberOfTrainer(trainerId, memberId))
                .message("내 회원으로 등록되었습니다.")
                .build();
    }

}
