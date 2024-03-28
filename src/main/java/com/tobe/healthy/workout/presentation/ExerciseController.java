package com.tobe.healthy.workout.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.workout.application.ExerciseService;
import com.tobe.healthy.workout.domain.dto.ExerciseDto;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryDto;
import com.tobe.healthy.workout.domain.entity.ExerciseCategory;
import com.tobe.healthy.workout.domain.entity.PrimaryMuscle;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exercise/v1")
@Tag(name = "Exercise", description = "운동종류 API")
@Slf4j
public class ExerciseController {

    private final ExerciseService exerciseService;

    @Operation(summary = "운동 종류 조회", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "운동 종류를 반환한다.")
    })
    @GetMapping
    public ResponseHandler<List<ExerciseDto>> getExercise(@RequestParam(required = false) ExerciseCategory category,
                                                          @RequestParam(required = false) PrimaryMuscle primaryMuscle,
                                                          Pageable pageable) {
        return ResponseHandler.<List<ExerciseDto>>builder()
                .data(exerciseService.getExercise(category, primaryMuscle, pageable))
                .message("운동 종류가 조회되었습니다.")
                .build();
    }

}
