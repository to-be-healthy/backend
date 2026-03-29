package com.tobe.healthy.diet.presentation;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tobe.healthy.common.CustomPaging;
import com.tobe.healthy.ApiResult;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.diet.application.DietCommentService;
import com.tobe.healthy.diet.presentation.dto.DietCommentDto;
import com.tobe.healthy.diet.presentation.dto.in.DietCommentAddCommand;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/diets")
@Tag(name = "09-00. 식단기록 댓글 API", description = "식단기록 댓글 API")
@Slf4j
public class DietCommentController {

	private final DietCommentService commentService;

	@Operation(summary = "식단기록의 댓글을 조회한다.", responses = {
		@ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
		@ApiResponse(responseCode = "200", description = "식단기록의 댓글, 페이징을 반환한다.")
	})
	@GetMapping("/{dietId}/comments")
	public ApiResult<CustomPaging<DietCommentDto>> getCommentsByDietId(
		@Parameter(description = "식단기록 ID") @PathVariable("dietId") Long dietId,
		Pageable pageable) {
		return ApiResult.<CustomPaging<DietCommentDto>>builder()
			.data(commentService.getCommentsByDietId(dietId, pageable))
			.message("댓글이 조회되었습니다.")
			.build();
	}

	@Operation(summary = "식단기록에 댓글(답글)을 등록한다", responses = {
		@ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
		@ApiResponse(responseCode = "200", description = "식단기록 댓글을 반환한다.")
	})
	@PostMapping("/{dietId}/comments")
	public ApiResult<Void> addComment(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@Parameter(description = "식단기록 ID") @PathVariable("dietId") Long dietId,
		@Valid @RequestBody DietCommentAddCommand command) {
		commentService.addComment(dietId, command, customMemberDetails.getMember());
		return ApiResult.<Void>builder()
			.message("댓글이 등록되었습니다.")
			.build();
	}

	@Operation(summary = "식단기록의 댓글을 수정한다.", responses = {
		@ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
		@ApiResponse(responseCode = "200", description = "식단기록 댓글을 반환한다.")
	})
	@PatchMapping("/{dietId}/comments/{commentId}")
	public ApiResult<DietCommentDto> updateComment(
		@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@Parameter(description = "식단기록 ID") @PathVariable("dietId") Long dietId,
		@Parameter(description = "식단기록의 댓글 ID") @PathVariable("commentId") Long commentId,
		@Valid @RequestBody DietCommentAddCommand command) {
		return ApiResult.<DietCommentDto>builder()
			.data(commentService.updateComment(customMemberDetails.getMember(), dietId, commentId, command))
			.message("댓글이 수정되었습니다.")
			.build();
	}

	@Operation(summary = "식단기록의 댓글을 삭제한다.", responses = {
		@ApiResponse(responseCode = "200", description = "식단기록 댓글 삭제 완료.")
	})
	@DeleteMapping("/{dietId}/comments/{commentId}")
	public ApiResult<Void> deleteComment(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
		@Parameter(description = "식단기록 ID") @PathVariable("dietId") Long dietId,
		@Parameter(description = "식단기록의 댓글 ID") @PathVariable("commentId") Long commentId) {
		commentService.deleteComment(customMemberDetails.getMember(), dietId, commentId);
		return ApiResult.<Void>builder()
			.message("댓글이 삭제되었습니다.")
			.build();
	}

}
