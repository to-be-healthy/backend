package com.tobe.healthy.trainer.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.trainer.application.TrainerService;
import com.tobe.healthy.trainer.domain.dto.TrainerMemberMappingDto;
import com.tobe.healthy.trainer.domain.dto.in.MemberInviteCommand;
import com.tobe.healthy.workout.application.WorkoutHistoryService;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trainers/v1")
@Tag(name = "04. 트레이너 API", description = "트레이너 API")
@Slf4j
public class TrainerController {

    private final TrainerService trainerService;
    private final WorkoutHistoryService workoutService;

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
    @PostMapping("/{trainerId}/members/{memberId}")
    public ResponseHandler<TrainerMemberMappingDto> addMemberOfTrainer(@PathVariable("trainerId") Long trainerId,
                                                                       @PathVariable("memberId") Long memberId) {
        return ResponseHandler.<TrainerMemberMappingDto>builder()
                .data(trainerService.addMemberOfTrainer(trainerId, memberId))
                .message("내 회원으로 등록되었습니다.")
                .build();
    }


    @Operation(summary = "트레이너 회원들의 운동기록 목록 조회", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "운동기록, 페이징을 반환한다.")
    })
    @GetMapping("/{trainerId}/workout-histories")
    public ResponseHandler<List<WorkoutHistoryDto>> getWorkoutHistoryByTrainer(@PathVariable("trainerId") Long trainerId,
                                                                               Pageable pageable) {
        return ResponseHandler.<List<WorkoutHistoryDto>>builder()
                .data(workoutService.getWorkoutHistoryByTrainer(trainerId, pageable))
                .message("운동기록이 조회되었습니다.")
                .build();
    }

}
