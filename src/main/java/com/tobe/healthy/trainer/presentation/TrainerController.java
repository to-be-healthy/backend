package com.tobe.healthy.trainer.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.gym.application.GymService;
import com.tobe.healthy.gym.domain.dto.MemberInTeamCommandResult;
import com.tobe.healthy.member.application.MemberService;
import com.tobe.healthy.member.domain.entity.AlarmStatus;
import com.tobe.healthy.trainer.application.TrainerService;
import com.tobe.healthy.trainer.domain.dto.TrainerMemberMappingDto;
import com.tobe.healthy.trainer.domain.dto.in.MemberInviteCommand;
import com.tobe.healthy.trainer.domain.dto.in.MemberLessonCommand;
import com.tobe.healthy.trainer.domain.dto.out.MemberInviteResultCommand;
import com.tobe.healthy.workout.application.WorkoutHistoryService;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trainers/v1")
@Tag(name = "05. 트레이너 API", description = "트레이너 API")
@Slf4j
public class TrainerController {

    private final TrainerService trainerService;
    private final WorkoutHistoryService workoutService;
    private final GymService gymService;
    private final MemberService memberService;

    @Operation(summary = "트레이너가 학생 초대하기", responses = {
		@ApiResponse(responseCode = "400", description = "시작날짜와 종료날짜가 유효하지않습니다."),
		@ApiResponse(responseCode = "400", description = "회원을 찾을 수 없습니다."),
		@ApiResponse(responseCode = "200", description = "회원초대가 완료 되었습니다.")
    })
    @PostMapping("/invitation")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public ResponseHandler<MemberInviteResultCommand> inviteMember(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
                                                                   @RequestBody MemberInviteCommand command) {
        return ResponseHandler.<MemberInviteResultCommand>builder()
                .data(trainerService.inviteMember(command, customMemberDetails.getMember()))
                .message("회원초대가 완료 되었습니다.")
                .build();
    }

    @Operation(summary = "트레이너가 내 학생으로 등록하기", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "매핑ID, 트레이너ID, 회원ID를 반환한다.")
    })
    @PostMapping("/{trainerId}/members/{memberId}")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public ResponseHandler<TrainerMemberMappingDto> addMemberOfTrainer(@Parameter(description = "트레이너 ID") @PathVariable("trainerId") Long trainerId,
                                                                       @Parameter(description = "학생 ID") @PathVariable("memberId") Long memberId,
                                                                       @RequestBody MemberLessonCommand command) {
        return ResponseHandler.<TrainerMemberMappingDto>builder()
                .data(trainerService.addMemberOfTrainer(trainerId, memberId, command))
                .message("내 학생으로 등록되었습니다.")
                .build();
    }


    @Operation(summary = "트레이너가 관리하는 학생들의 운동기록 목록 조회하기", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "운동기록, 페이징을 반환한다.")
    })
    @GetMapping("/{trainerId}/workout-histories")
    public ResponseHandler<List<WorkoutHistoryDto>> getWorkoutHistoryByTrainer(@Parameter(description = "트레이너 ID") @PathVariable("trainerId") Long trainerId,
                                                                               Pageable pageable) {
        return ResponseHandler.<List<WorkoutHistoryDto>>builder()
                .data(workoutService.getWorkoutHistoryByTrainer(trainerId, pageable))
                .message("운동기록이 조회되었습니다.")
                .build();
    }

    @Operation(summary = "트레이너가 관리하는 학생들을 조회한다.", description = "트레이너가 관리하는 학생 전체를 조회한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "트레이너가 관리하는 학생 조회 완료")
            })
    @GetMapping("/members")
    public ResponseHandler<List<MemberInTeamCommandResult>> findAllMyMemberInTeam(@AuthenticationPrincipal CustomMemberDetails member) {
        return ResponseHandler.<List<MemberInTeamCommandResult>>builder()
                .data(gymService.findAllMyMemberInTeam(member.getMemberId()))
                .message("트레이너가 관리하는 학생을 조회하였습니다.")
                .build();
    }

    @Operation(summary = "수업 기록 여부를 변경한다.", description = "트레이너가 사용하는 수업기록여부를 변경한다.",
            responses = {
                    @ApiResponse(responseCode = "404", description = "등록된 회원이 아닙니다."),
                    @ApiResponse(responseCode = "200", description = "수업 기록 여부가 변경되었습니다.")
            })
    @PatchMapping("/trainer-feedback")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public ResponseHandler<Boolean> changeTrainerFeedback(@Parameter(description = "변경할 수업 기록 상태", example = "ENABLED") @RequestParam AlarmStatus alarmStatus,
                                                          @AuthenticationPrincipal CustomMemberDetails member) {
        return ResponseHandler.<Boolean>builder()
                .data(memberService.changeTrainerFeedback(alarmStatus, member.getMemberId()))
                .message("수업 기록 여부가 변경되었습니다.")
                .build();
    }
}
