package com.tobe.healthy.workout.presentation;

import com.tobe.healthy.workout.application.WorkoutService;
import com.tobe.healthy.workout.domain.dto.in.WorkoutHistoryAddCommand;
import com.tobe.healthy.workout.domain.dto.out.WorkoutHistoryAddCommandResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workout")
@Slf4j
public class WorkoutController {

    private final WorkoutService workoutService;

    @PostMapping
    public ResponseEntity<WorkoutHistoryAddCommandResult> addWorkoutHistory(@RequestBody WorkoutHistoryAddCommand request) {
        WorkoutHistoryAddCommandResult response = workoutService.addWorkoutHistory(request);
        return ResponseEntity.ok(response);
    }

}
