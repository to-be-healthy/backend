package com.tobe.healthy.workout.presentation;

import com.tobe.healthy.common.CommonService;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.workout.application.WorkoutService;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryDto;
import com.tobe.healthy.workout.domain.dto.in.WorkoutHistoryAddCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class WorkoutController {

    private final CommonService commonService;
    private final WorkoutService workoutService;

    @PostMapping("/workout-histories")
    public ResponseEntity<?> addWorkoutHistory(@RequestHeader(name="Authorization") String bearerToken,
                                               @Valid WorkoutHistoryAddCommand command) {
        Member member = commonService.getMemberIdByToken(bearerToken);
        return ResponseEntity.ok(workoutService.addWorkoutHistory(member, command));
    }

    @GetMapping("/workout-histories")
    public ResponseEntity<?> getWorkoutHistory(@RequestParam("memberId") Long memberId, Pageable pageable) {
        List<WorkoutHistoryDto> response = workoutService.getWorkoutHistory(memberId, pageable);
        return ResponseEntity.ok(response);
    }

}
