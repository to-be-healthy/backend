package com.tobe.healthy.workout.presentation;

import com.tobe.healthy.common.CommonService;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.workout.application.CommentService;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryCommentDto;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryDto;
import com.tobe.healthy.workout.domain.dto.in.HistoryAddCommand;
import com.tobe.healthy.workout.domain.dto.in.HistoryCommentAddCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class CommentController {

    private final CommonService commonService;
    private final CommentService commentService;

    @Operation(summary = "운동기록 댓글 등록", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "운동기록 댓글을 반환한다.")
    })
    @PostMapping("/workout-histories/{workoutHistoryId}/comments")
    public ResponseEntity<WorkoutHistoryCommentDto> addComment(@RequestHeader(name="Authorization") String bearerToken,
                                                                @PathVariable("workoutHistoryId") Long workoutHistoryId,
                                                                @Valid HistoryCommentAddCommand command) {
        Member member = commonService.getMemberByToken(bearerToken);
        return ResponseEntity.ok(commentService.addComments(workoutHistoryId, command, member));
    }

    @Operation(summary = "운동기록 댓글 수정", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "운동기록 댓글을 반환한다.")
    })
    @PatchMapping("/workout-histories/{workoutHistoryId}/comments/{commentId}")
    public ResponseEntity<WorkoutHistoryCommentDto> updateComment(@RequestHeader(name="Authorization") String bearerToken,
                                                                  @PathVariable("workoutHistoryId") Long workoutHistoryId,
                                                                  @PathVariable("commentId") Long commentId,
                                                                  @Valid HistoryCommentAddCommand command) {
        Member member = commonService.getMemberByToken(bearerToken);
        return ResponseEntity.ok(commentService.updateComment(member, workoutHistoryId, commentId, command));
    }

}
