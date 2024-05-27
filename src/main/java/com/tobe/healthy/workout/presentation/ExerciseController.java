package com.tobe.healthy.workout.presentation;

import com.tobe.healthy.common.CustomPaging;
import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.workout.application.ExerciseService;
import com.tobe.healthy.workout.domain.dto.ExerciseDto;
import com.tobe.healthy.workout.domain.dto.in.CustomExerciseAddCommand;
import com.tobe.healthy.workout.domain.dto.out.ExerciseCategoryDto;
import com.tobe.healthy.workout.domain.entity.exercise.ExerciseCategory;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exercise/v1")
@Tag(name = "06-00. 운동종류 API", description = "운동종류 API")
@Slf4j
public class ExerciseController {

    private final ExerciseService exerciseService;

    @Operation(summary = "운동 카레고리 조회", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "운동 종류를 반환한다.")
    })
    @GetMapping("/category")
    public ResponseHandler<List<ExerciseCategoryDto>> getExerciseCategory() {
        return ResponseHandler.<List<ExerciseCategoryDto>>builder()
                .data(ExerciseCategory.getCategoryList())
                .message("운동 카테고리가 조회되었습니다.")
                .build();
    }

    @Operation(summary = "운동 종류 목록 조회", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "운동 종류를 반환한다.")
    })
    @GetMapping
    public ResponseHandler<CustomPaging<ExerciseDto>> getExercise(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
                                                                  @Parameter(description = "카테고리") @RequestParam(required = false) ExerciseCategory exerciseCategory,
                                                                  Pageable pageable) {
        return ResponseHandler.<CustomPaging<ExerciseDto>>builder()
                .data(exerciseService.getExercise(customMemberDetails.getMember(), exerciseCategory, pageable))
                .message("운동 종류가 조회되었습니다.")
                .build();
    }

    @Operation(summary = "운동 종류 추가", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "운동 종류를 등록한다.")
    })
    @PostMapping
    public ResponseHandler<Void> addExerciseCustom(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
                                                                        @Valid @RequestBody CustomExerciseAddCommand command) {
        exerciseService.addExerciseCustom(customMemberDetails.getMember(), command);
        return ResponseHandler.<Void>builder()
                .message("운동 종류가 등록되었습니다.")
                .build();
    }

    @Operation(summary = "운동 종류 삭제", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "운동 종류를 삭제한다.")
    })
    @DeleteMapping("/{exerciseId}")
    public ResponseHandler<Void> deleteExerciseCustom(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
                                                      @Parameter(description = "운동종류 ID") @PathVariable("exerciseId") Long exerciseId) {
        exerciseService.deleteExerciseCustom(customMemberDetails.getMember(), exerciseId);
        return ResponseHandler.<Void>builder()
                .message("운동 종류가 삭제되었습니다.")
                .build();
    }

}
