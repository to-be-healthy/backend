package com.tobe.healthy.workout.presentation;

import com.tobe.healthy.common.CommonService;
import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.workout.application.WorkoutHistoryService;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryDto;
import com.tobe.healthy.workout.domain.dto.in.HistoryAddCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/workout-histories/v1")
@Tag(name = "06-01. 운동기록 API", description = "운동기록 API")
@Slf4j
public class WorkoutHistoryController {

    private final WorkoutHistoryService workoutService;

    @Operation(summary = "운동기록 등록", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "운동기록ID, 회원ID, 운동기록 내용을 반환한다.")
    })
    @PostMapping
    public ResponseHandler<WorkoutHistoryDto> addWorkoutHistory(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
                                                                @Valid HistoryAddCommand command) {
        return ResponseHandler.<WorkoutHistoryDto>builder()
                .data(workoutService.addWorkoutHistory(customMemberDetails.getMember(), command))
                .message("운동기록이 등록되었습니다.")
                .build();
    }

    @Operation(summary = "운동기록 상세 조회", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "운동기록 상세정보를 반환한다.")
    })
    @GetMapping("/{workoutHistoryId}")
    public ResponseHandler<WorkoutHistoryDto> getWorkoutHistoryDetail(@Parameter(description = "운동기록 ID") @PathVariable("workoutHistoryId") Long workoutHistoryId) {
        return ResponseHandler.<WorkoutHistoryDto>builder()
                .data(workoutService.getWorkoutHistoryDetail(workoutHistoryId))
                .message("운동기록이 조회되었습니다.")
                .build();
    }

    @Operation(summary = "운동기록 삭제", responses = {
            @ApiResponse(responseCode = "200", description = "운동기록 삭제 완료.")
    })
    @PatchMapping("/{workoutHistoryId}")
    public ResponseHandler<Void> deleteWorkoutHistory(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
                                                      @Parameter(description = "운동기록 ID") @PathVariable("workoutHistoryId") Long workoutHistoryId) {
        workoutService.deleteWorkoutHistory(customMemberDetails.getMember(), workoutHistoryId);
        return ResponseHandler.<Void>builder()
                .message("운동기록이 삭제되었습니다.")
                .build();
    }

    @Operation(summary = "운동기록 수정", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "운동기록ID, 회원ID, 운동기록 내용을 반환한다.")
    })
    @PutMapping("/{workoutHistoryId}")
    public ResponseHandler<WorkoutHistoryDto> updateWorkoutHistory(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
                                                                   @Parameter(description = "운동기록 ID") @PathVariable("workoutHistoryId") Long workoutHistoryId,
                                                                  @Valid HistoryAddCommand command) {
        return ResponseHandler.<WorkoutHistoryDto>builder()
                .data(workoutService.updateWorkoutHistory(customMemberDetails.getMember(), workoutHistoryId, command))
                .message("운동기록이 수정되었습니다.")
                .build();
    }

    @Operation(summary = "운동기록 좋아요", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "좋아요 완료.")
    })
    @PostMapping("/{workoutHistoryId}/like")
    public ResponseHandler<Void> likeWorkoutHistory(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
                                                    @Parameter(description = "운동기록 ID") @PathVariable("workoutHistoryId") Long workoutHistoryId) {
        workoutService.likeWorkoutHistory(customMemberDetails.getMember(), workoutHistoryId);
        return ResponseHandler.<Void>builder()
                .message("운동기록 좋아요에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "운동기록 좋아요 취소", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "좋아요 취소 완료.")
    })
    @DeleteMapping("/{workoutHistoryId}/like")
    public ResponseHandler<Void> deleteLikeWorkoutHistory(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
                                                          @Parameter(description = "운동기록 ID") @PathVariable("workoutHistoryId") Long workoutHistoryId) {
        workoutService.deleteLikeWorkoutHistory(customMemberDetails.getMember(), workoutHistoryId);
        return ResponseHandler.<Void>builder()
                .message("운동기록 좋아요가 취소되었습니다.")
                .build();
    }

}
