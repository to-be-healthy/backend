package com.tobe.healthy.diet.presentation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.query.Param;
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
import com.tobe.healthy.diet.application.DietService;
import com.tobe.healthy.diet.presentation.dto.DietDto;
import com.tobe.healthy.diet.presentation.dto.in.DietAddCommand;
import com.tobe.healthy.diet.presentation.dto.in.DietAddCommandAtHome;
import com.tobe.healthy.diet.presentation.dto.in.DietUpdateCommand;
import com.tobe.healthy.diet.presentation.dto.out.DietUploadDaysResult;
import com.tobe.healthy.workout.application.FileService;
import com.tobe.healthy.workout.presentation.dto.in.RegisterFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/diets")
@Tag(name = "09. 식단 API", description = "식단 API")
@Slf4j
public class DietController {

	private final DietService dietService;
	private final FileService fileService;

	@Operation(summary = "식단기록 첨부파일 등록")
	@PostMapping("/file")
	public ApiResult<List<RegisterFile>> addDietFile(
		@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@Valid List<MultipartFile> uploadFiles) {
		return ApiResult.<List<RegisterFile>>builder()
			.data(fileService.uploadFiles("diet", uploadFiles, customMemberDetails.getMember()))
			.message("첨부파일이 등록되었습니다.")
			.build();
	}

	@Operation(summary = "홈에서 식단기록 등록")
	@PostMapping("/home")
	public ApiResult<DietDto> addDietAtHome(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@Valid @RequestBody DietAddCommandAtHome command) {
		return ApiResult.<DietDto>builder()
			.data(dietService.addDietAtHome(customMemberDetails.getMember(), command))
			.message("식단기록이 등록되었습니다.")
			.build();
	}

	@Operation(summary = "식단기록 등록")
	@PostMapping
	public ApiResult<DietDto> addDiet(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@RequestBody @Valid DietAddCommand command) {
		return ApiResult.<DietDto>builder()
			.data(dietService.addDiet(customMemberDetails.getMember(), command))
			.message("식단기록이 등록되었습니다.")
			.build();
	}

	@Operation(summary = "오늘 식단 조회")
	@GetMapping("/today")
	public ApiResult<DietDto> getTodayDiet(@AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
		return ApiResult.<DietDto>builder()
			.data(dietService.getTodayDiet(customMemberDetails.getMember().getId()))
			.message("식단기록이 조회되었습니다.")
			.build();
	}

	@Operation(summary = "식단기록 상세 조회")
	@GetMapping("/{dietId}")
	public ApiResult<DietDto> getDietDetail(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@Parameter(description = "식단기록 ID") @PathVariable("dietId") Long dietId) {
		return ApiResult.<DietDto>builder()
			.data(dietService.getDietDetail(customMemberDetails.getMemberId(), dietId))
			.message("식단기록이 조회되었습니다.")
			.build();
	}

	@Operation(summary = "식단기록 좋아요")
	@PostMapping("/{dietId}/like")
	public ApiResult<Void> likeDiet(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@Parameter(description = "식단기록 ID") @PathVariable("dietId") Long dietId) {
		dietService.likeDiet(customMemberDetails.getMember(), dietId);
		return ApiResult.<Void>builder()
			.message("식단기록 좋아요에 성공하였습니다.")
			.build();
	}

	@Operation(summary = "식단기록 좋아요 취소")
	@DeleteMapping("/{dietId}/like")
	public ApiResult<Void> deleteLikeDiet(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@Parameter(description = "식단기록 ID") @PathVariable("dietId") Long dietId) {
		dietService.deleteLikeDiet(customMemberDetails.getMember(), dietId);
		return ApiResult.<Void>builder()
			.message("식단기록 좋아요가 취소되었습니다.")
			.build();
	}

	@Operation(summary = "식단기록 삭제")
	@DeleteMapping("/{dietId}")
	public ApiResult<Void> deleteDiet(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@Parameter(description = "식단기록 ID") @PathVariable("dietId") Long dietId) {
		dietService.deleteDiet(customMemberDetails.getMember(), dietId);
		return ApiResult.<Void>builder()
			.message("식단기록이 삭제되었습니다.")
			.build();
	}

	@Operation(summary = "식단기록 수정")
	@PatchMapping("/{dietId}")
	public ApiResult<DietDto> updateDiet(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@Parameter(description = "식단기록 ID") @PathVariable("dietId") Long dietId,
		@RequestBody @Valid DietUpdateCommand command) {
		return ApiResult.<DietDto>builder()
			.data(dietService.updateDiet(customMemberDetails.getMember(), dietId, command))
			.message("식단기록이 수정되었습니다.")
			.build();
	}

	@Operation(summary = "식단 등록한 날짜 조회")
	@GetMapping("/upload-date")
	public ApiResult<DietUploadDaysResult> getDietUploadDays(
		@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@Parameter(description = "시작 날짜", example = "2024-03-01") @Param("startDate") LocalDate startDate,
		@Parameter(description = "종료 날짜", example = "2024-05-31") @Param("endDate") LocalDate endDate) {
		return ApiResult.<DietUploadDaysResult>builder()
			.data(dietService.getDietUploadDays(customMemberDetails.getMember().getId(), startDate, endDate))
			.message("업로드 날짜가 조회되었습니다.")
			.build();
	}

}
