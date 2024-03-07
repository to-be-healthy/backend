package com.tobe.healthy.workout.presentation;

import com.tobe.healthy.common.CommonService;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.workout.application.WorkoutHistoryService;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryDto;
import com.tobe.healthy.workout.domain.dto.in.HistoryAddCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class WorkoutHistoryController {

    private final CommonService commonService;
    private final WorkoutHistoryService workoutService;

    @Operation(summary = "운동기록 등록", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "운동기록ID, 회원ID, 운동기록 내용을 반환한다.")
    })
    @PostMapping("/workout-histories")
    public ResponseEntity<WorkoutHistoryDto> addWorkoutHistory(@RequestHeader(name="Authorization") String bearerToken,
                                                               @Valid HistoryAddCommand command) {
        Member member = commonService.getMemberByToken(bearerToken);
        return ResponseEntity.ok(workoutService.addWorkoutHistory(member, command));
    }

    @Operation(summary = "회원 운동기록 목록 조회", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "운동기록, 페이징을 반환한다.")
    })
    @GetMapping("/members/{memberId}/workout-histories")
    public ResponseEntity<List<WorkoutHistoryDto>> getWorkoutHistory(@PathVariable("memberId") Long memberId,
                                                                     Pageable pageable) {
        return ResponseEntity.ok(workoutService.getWorkoutHistory(memberId, pageable));
    }

    @Operation(summary = "트레이너 회원들의 운동기록 목록 조회", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "운동기록, 페이징을 반환한다.")
    })
    @GetMapping("/trainers/{trainerId}/workout-histories")
    public ResponseEntity<List<WorkoutHistoryDto>> getWorkoutHistoryByTrainer(@PathVariable("trainerId") Long trainerId,
                                                                              Pageable pageable) {
        return ResponseEntity.ok(workoutService.getWorkoutHistoryByTrainer(trainerId, pageable));
    }

    @Operation(summary = "운동기록 상세 조회", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "운동기록 상세정보를 반환한다.")
    })
    @GetMapping("/workout-histories/{workoutHistoryId}")
    public ResponseEntity<WorkoutHistoryDto> getWorkoutHistoryDetail(@PathVariable("workoutHistoryId") Long workoutHistoryId) {
        return ResponseEntity.ok(workoutService.getWorkoutHistoryDetail(workoutHistoryId));
    }

    @Operation(summary = "운동기록 삭제", responses = {
            @ApiResponse(responseCode = "200", description = "운동기록 삭제 완료.")
    })
    @DeleteMapping("/workout-histories/{workoutHistoryId}")
    public ResponseEntity<?> deleteWorkoutHistory(@PathVariable("workoutHistoryId") Long workoutHistoryId) {
        workoutService.deleteWorkoutHistory(workoutHistoryId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "운동기록 수정", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "운동기록ID, 회원ID, 운동기록 내용을 반환한다.")
    })
    @PutMapping("/workout-histories/{workoutHistoryId}")
    public ResponseEntity<WorkoutHistoryDto> updateWorkoutHistory(@RequestHeader(name="Authorization") String bearerToken,
                                                                            @PathVariable("workoutHistoryId") Long workoutHistoryId,
                                                                            @Valid HistoryAddCommand command) {
        Member member = commonService.getMemberByToken(bearerToken);
        return ResponseEntity.ok(workoutService.updateWorkoutHistory(member, workoutHistoryId, command));
    }

}
