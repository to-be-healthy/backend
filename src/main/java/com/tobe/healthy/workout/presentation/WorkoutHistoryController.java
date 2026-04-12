package com.tobe.healthy.workout.presentation;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tobe.healthy.ApiResult;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.workout.application.FileService;
import com.tobe.healthy.workout.application.WorkoutHistoryService;
import com.tobe.healthy.workout.presentation.dto.in.HistoryAddCommand;
import com.tobe.healthy.workout.presentation.dto.in.RegisterFile;
import com.tobe.healthy.workout.presentation.dto.out.WorkoutHistoryDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/workout-histories")
@Tag(name = "06-01. 운동기록 API", description = "운동기록 API")
@Slf4j
public class WorkoutHistoryController {

	private final WorkoutHistoryService workoutService;
	private final FileService fileService;

	@Operation(summary = "운동기록 첨부파일 등록")
	@PostMapping("/file")
	public ApiResult<List<RegisterFile>> addWorkoutHistoryFile(
		@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@Valid List<MultipartFile> uploadFiles) {
		return ApiResult.<List<RegisterFile>>builder()
			.data(fileService.uploadFiles("workout-history", uploadFiles, customMemberDetails.getMember()))
			.message("첨부파일이 등록되었습니다.")
			.build();
	}

	@Operation(summary = "운동기록 등록")
	@PostMapping
	public ApiResult<WorkoutHistoryDto> addWorkoutHistory(
		@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@RequestBody @Valid HistoryAddCommand request) {
		return ApiResult.<WorkoutHistoryDto>builder()
			.data(workoutService.addWorkoutHistory(customMemberDetails.getMember(), request))
			.message("운동기록이 등록되었습니다.")
			.build();
	}

	@Operation(summary = "운동기록 상세 조회")
	@GetMapping("/{workoutHistoryId}")
	public ApiResult<WorkoutHistoryDto> getWorkoutHistoryDetail(
		@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@Parameter(description = "운동기록 ID") @PathVariable("workoutHistoryId") Long workoutHistoryId) {
		return ApiResult.<WorkoutHistoryDto>builder()
			.data(workoutService.getWorkoutHistoryDetail(customMemberDetails.getMember(), workoutHistoryId))
			.message("운동기록이 조회되었습니다.")
			.build();
	}

	@Operation(summary = "운동기록 삭제")
	@DeleteMapping("/{workoutHistoryId}")
	public ApiResult<Void> deleteWorkoutHistory(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@Parameter(description = "운동기록 ID") @PathVariable("workoutHistoryId") Long workoutHistoryId) {
		workoutService.deleteWorkoutHistory(customMemberDetails.getMember(), workoutHistoryId);
		return ApiResult.<Void>builder()
			.message("운동기록이 삭제되었습니다.")
			.build();
	}

	@Operation(summary = "운동기록 수정")
	@PatchMapping("/{workoutHistoryId}")
	public ApiResult<WorkoutHistoryDto> updateWorkoutHistory(
		@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@Parameter(description = "운동기록 ID") @PathVariable("workoutHistoryId") Long workoutHistoryId,
		@RequestBody @Valid HistoryAddCommand command) {
		return ApiResult.<WorkoutHistoryDto>builder()
			.data(workoutService.updateWorkoutHistory(customMemberDetails.getMember(), workoutHistoryId, command))
			.message("운동기록이 수정되었습니다.")
			.build();
	}

	@Operation(summary = "운동기록 좋아요")
	@PostMapping("/{workoutHistoryId}/like")
	public ApiResult<Void> likeWorkoutHistory(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@Parameter(description = "운동기록 ID") @PathVariable("workoutHistoryId") Long workoutHistoryId) {
		workoutService.likeWorkoutHistory(customMemberDetails.getMember(), workoutHistoryId);
		return ApiResult.<Void>builder()
			.message("운동기록 좋아요에 성공하였습니다.")
			.build();
	}

	@Operation(summary = "운동기록 좋아요 취소")
	@DeleteMapping("/{workoutHistoryId}/like")
	public ApiResult<Void> deleteLikeWorkoutHistory(
		@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@Parameter(description = "운동기록 ID") @PathVariable("workoutHistoryId") Long workoutHistoryId) {
		workoutService.deleteLikeWorkoutHistory(customMemberDetails.getMember(), workoutHistoryId);
		return ApiResult.<Void>builder()
			.message("운동기록 좋아요가 취소되었습니다.")
			.build();
	}

}
