package com.tobe.healthy.diet.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.workout.application.ExerciseService;
import com.tobe.healthy.workout.domain.dto.ExerciseDto;
import com.tobe.healthy.workout.domain.entity.ExerciseCategory;
import com.tobe.healthy.workout.domain.entity.PrimaryMuscle;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diet/v1")
@Tag(name = "식단 API", description = "식단 API")
@Slf4j
public class DietController {

//    @Operation(summary = "식단 조회", responses = {
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
//            @ApiResponse(responseCode = "200", description = "운동 종류를 반환한다.")
//    })
//    @GetMapping
//    public ResponseHandler<List<ExerciseDto>> getExercise(@Parameter(description = "운동 카테고리") @RequestParam(required = false) ExerciseCategory category,
//                                                          @Parameter(description = "사용하는 근육") @RequestParam(required = false) PrimaryMuscle primaryMuscle,
//                                                          Pageable pageable) {
//        return ResponseHandler.<List<ExerciseDto>>builder()
//                .data(exerciseService.getExercise(category, primaryMuscle, pageable))
//                .message("운동 종류가 조회되었습니다.")
//                .build();
//    }

}
