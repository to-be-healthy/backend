package com.tobe.healthy.workout.presentation;

import com.tobe.healthy.common.CommonService;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.workout.application.CommentService;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryCommentDto;
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
            @ApiResponse(responseCode = "200", description = "운동기록, 페이징을 반환한다.")
    })
    @PostMapping("/workout-histories/{workoutHistoryId}/comments")
    public ResponseEntity<WorkoutHistoryCommentDto> addComments(@RequestHeader(name="Authorization") String bearerToken,
                                                                @PathVariable("workoutHistoryId") Long workoutHistoryId,
                                                                @Valid HistoryCommentAddCommand command) {
        Member member = commonService.getMemberByToken(bearerToken);
        return ResponseEntity.ok(commentService.addComments(workoutHistoryId, command, member));
    }

}
