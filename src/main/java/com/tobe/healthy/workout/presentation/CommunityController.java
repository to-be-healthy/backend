package com.tobe.healthy.workout.presentation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tobe.healthy.common.CustomPaging;
import com.tobe.healthy.ApiResult;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.workout.application.WorkoutHistoryService;
import com.tobe.healthy.workout.presentation.dto.out.WorkoutHistoryDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community")
@Tag(name = "06-04. 커뮤니티 API", description = "커뮤니티 API")
@Slf4j
public class CommunityController {

	private final WorkoutHistoryService workoutService;

	@Operation(summary = "커뮤니티 운동기록 목록 조회하기", responses = {
		@ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
		@ApiResponse(responseCode = "200", description = "운동기록, 페이징을 반환한다.")
	})
	@GetMapping
	public ApiResult<CustomPaging<WorkoutHistoryDto>> getWorkoutHistoryOnCommunity(
		@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@Parameter(description = "조회할 날짜", example = "2024-12") @Param("searchDate") String searchDate,
		@Parameter(description = "학생 ID") Long memberId,
		Pageable pageable) {
		return ApiResult.<CustomPaging<WorkoutHistoryDto>>builder()
			.data(workoutService.getWorkoutHistoryOnCommunity(memberId, customMemberDetails.getMember(), pageable,
				searchDate))
			.message("운동기록이 조회되었습니다.")
			.build();
	}

}
