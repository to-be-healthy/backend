package com.tobe.healthy.workout.presentation;

import com.tobe.healthy.common.CommonService;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.workout.application.CommentService;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryCommentDto;
import com.tobe.healthy.workout.domain.dto.in.HistoryCommentAddCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "comment", description = "운동기록 댓글 API")
@Slf4j
public class CommentController {

    private final CommonService commonService;
    private final CommentService commentService;

    @Operation(summary = "운동기록 댓글 조회", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "운동기록의 댓글, 페이징을 반환한다.")
    })
    @GetMapping("/workout-histories/{workoutHistoryId}/comments")
    public ResponseEntity<List<WorkoutHistoryCommentDto>> getCommentsByHistoryId(@PathVariable("workoutHistoryId") Long workoutHistoryId,
                                                                                 Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByWorkoutHistoryId(workoutHistoryId, pageable));
    }

    @Operation(summary = "운동기록 댓글 등록", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "운동기록 댓글을 반환한다.")
    })
    @PostMapping("/workout-histories/{workoutHistoryId}/comments")
    public ResponseEntity<WorkoutHistoryCommentDto> addComment(@RequestHeader(name="Authorization") String bearerToken,
                                                                @PathVariable("workoutHistoryId") Long workoutHistoryId,
                                                                @Valid HistoryCommentAddCommand command) {
        Member member = commonService.getMemberByToken(bearerToken);
        return ResponseEntity.ok(commentService.addComment(workoutHistoryId, command, member));
    }

    @Operation(summary = "운동기록 댓글 수정", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "운동기록 댓글을 반환한다.")
    })
    @PutMapping("/workout-histories/{workoutHistoryId}/comments/{commentId}")
    public ResponseEntity<WorkoutHistoryCommentDto> updateComment(@RequestHeader(name="Authorization") String bearerToken,
                                                                  @PathVariable("workoutHistoryId") Long workoutHistoryId,
                                                                  @PathVariable("commentId") Long commentId,
                                                                  @Valid HistoryCommentAddCommand command) {
        Member member = commonService.getMemberByToken(bearerToken);
        return ResponseEntity.ok(commentService.updateComment(member, workoutHistoryId, commentId, command));
    }

    @Operation(summary = "운동기록 댓글 삭제", responses = {
            @ApiResponse(responseCode = "200", description = "운동기록 댓글 삭제 완료.")
    })
    @PatchMapping("/workout-histories/{workoutHistoryId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@RequestHeader(name="Authorization") String bearerToken,
                                           @PathVariable("workoutHistoryId") Long workoutHistoryId,
                                           @PathVariable("commentId") Long commentId) {
        Member member = commonService.getMemberByToken(bearerToken);
        commentService.deleteComment(member, workoutHistoryId, commentId);
        return ResponseEntity.ok().build();
    }

}
