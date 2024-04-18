package com.tobe.healthy.diet.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.diet.application.DietService;
import com.tobe.healthy.diet.domain.dto.in.DietAddCommand;
import com.tobe.healthy.workout.application.ExerciseService;
import com.tobe.healthy.workout.domain.dto.ExerciseDto;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryDto;
import com.tobe.healthy.workout.domain.dto.in.HistoryAddCommand;
import com.tobe.healthy.workout.domain.entity.ExerciseCategory;
import com.tobe.healthy.workout.domain.entity.PrimaryMuscle;
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
@RequestMapping("/diet/v1")
@Tag(name = "식단 API", description = "식단 API")
@Slf4j
public class DietController {

    private final DietService dietService;

//    @Operation(summary = "식단기록 등록", responses = {
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
//            @ApiResponse(responseCode = "200", description = "식단기록 내용을 반환한다.")
//    })
//    @PostMapping
//    public ResponseHandler<WorkoutHistoryDto> addDiet(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
//                                                      @Valid DietAddCommand command) {
//        return ResponseHandler.<WorkoutHistoryDto>builder()
//                .data(dietService.addDiet(customMemberDetails.getMember(), command))
//                .message("운동기록이 등록되었습니다.")
//                .build();
//    }

}
