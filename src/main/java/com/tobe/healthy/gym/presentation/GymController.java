package com.tobe.healthy.gym.presentation;

import com.tobe.healthy.ApiResult;
import com.tobe.healthy.gym.application.GymService;
import com.tobe.healthy.gym.domain.dto.out.GymResult;
import com.tobe.healthy.gym.domain.dto.out.TrainersByGymResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/gyms/v1")
@RequiredArgsConstructor
@Tag(name = "04-01.헬스장 API", description = "헬스장 조회 API")
public class GymController {

    private final GymService gymService;

    @Operation(
            summary = "모든 헬스장을 조회한다.",
            description = "등록된 모든 헬스장을 조회한다.",
            responses = @ApiResponse(responseCode = "200", description = "모든 헬스장을 조회한다.")
    )
    @GetMapping
    public ApiResult<List<GymResult>> findAllGym() {
        return ApiResult.<List<GymResult>>builder()
                .data(gymService.findAllGym())
                .message("모든 헬스장을 조회하였습니다.")
                .build();
    }

    @Operation(
            summary = "학생이 헬스장의 모든 트레이너들을 조회한다.",
            description = "학생이 헬스장의 모든 트레이너들을 조회한다.(새로운 트레이너가 상단에 있도록)",
            responses = @ApiResponse(responseCode = "200", description = "헬스장에 모든 트레이너 조회완료")
    )
    @GetMapping("/{gymId}/trainers")
    public ApiResult<List<TrainersByGymResult>> findAllTrainersByGym(
            @PathVariable Long gymId) {
        return ApiResult.<List<TrainersByGymResult>>builder()
                .data(gymService.findAllTrainersByGym(gymId))
                .message("헬스장의 모든 트레이너들을 조회하였습니다.")
                .build();
    }
}
