package com.tobe.healthy.workout.presentation;

import com.tobe.healthy.common.CustomPaging;
import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.workout.application.WorkoutCommentService;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryCommentDto;
import com.tobe.healthy.workout.domain.dto.in.HistoryCommentAddCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workout-histories/v1")
@Tag(name = "06-02. 운동기록 댓글 API", description = "운동기록 댓글 API")
@Slf4j
public class WorkoutCommentController {

    private final WorkoutCommentService commentService;

    @Operation(summary = "운동기록의 댓글을 조회한다.", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "운동기록의 댓글, 페이징을 반환한다.")
    })
    @GetMapping("/{workoutHistoryId}/comments")
    public ResponseHandler<CustomPaging<WorkoutHistoryCommentDto>> getCommentsByHistoryId(@Parameter(description = "운동기록 ID") @PathVariable("workoutHistoryId") Long workoutHistoryId,
                                                                                  Pageable pageable) {
        return ResponseHandler.<CustomPaging<WorkoutHistoryCommentDto>>builder()
                .data(commentService.getCommentsByWorkoutHistoryId(workoutHistoryId, pageable))
                .message("댓글이 조회되었습니다.")
                .build();
    }

    @Operation(summary = "운동기록에 댓글(답글)을 등록한다", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "운동기록 댓글을 반환한다.")
    })
    @PostMapping("/{workoutHistoryId}/comments")
    public ResponseHandler<Void> addComment(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
                                                                @Parameter(description = "운동기록 ID") @PathVariable("workoutHistoryId") Long workoutHistoryId,
                                                                @Valid @RequestBody HistoryCommentAddCommand command) {
        commentService.addComment(workoutHistoryId, command, customMemberDetails.getMember());
        return ResponseHandler.<Void>builder()
                .message("댓글이 등록되었습니다.")
                .build();
    }

    @Operation(summary = "운동기록의 댓글을 수정한다.", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "운동기록 댓글을 반환한다.")
    })
    @PatchMapping("/{workoutHistoryId}/comments/{commentId}")
    public ResponseHandler<WorkoutHistoryCommentDto> updateComment(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
                                                                   @Parameter(description = "운동기록 ID") @PathVariable("workoutHistoryId") Long workoutHistoryId,
                                                                   @Parameter(description = "운동기록의 댓글 ID") @PathVariable("commentId") Long commentId,
                                                                  @Valid @RequestBody HistoryCommentAddCommand command) {
        return ResponseHandler.<WorkoutHistoryCommentDto>builder()
                .data(commentService.updateComment(customMemberDetails.getMember(), workoutHistoryId, commentId, command))
                .message("댓글이 수정되었습니다.")
                .build();
    }

    @Operation(summary = "운동기록의 댓글을 삭제한다.", responses = {
            @ApiResponse(responseCode = "200", description = "운동기록 댓글 삭제 완료.")
    })
    @DeleteMapping("/{workoutHistoryId}/comments/{commentId}")
    public ResponseHandler<Void> deleteComment(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
                                               @Parameter(description = "운동기록 ID")@PathVariable("workoutHistoryId") Long workoutHistoryId,
                                               @Parameter(description = "운동기록의 댓글 ID")@PathVariable("commentId") Long commentId) {
        commentService.deleteComment(customMemberDetails.getMember(), workoutHistoryId, commentId);
        return ResponseHandler.<Void>builder()
                .message("댓글이 삭제되었습니다.")
                .build();
    }

}
