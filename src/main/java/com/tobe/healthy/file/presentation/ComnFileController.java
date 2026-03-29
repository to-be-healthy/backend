package com.tobe.healthy.file.presentation;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tobe.healthy.ApiResult;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.file.application.ComnFileService;
import com.tobe.healthy.file.presentation.dto.in.CommandUploadFile;
import com.tobe.healthy.workout.presentation.dto.in.RegisterFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
public class ComnFileController {

	private final ComnFileService comnFileService;

	@PostMapping
	public ApiResult<List<RegisterFile>> uploadFile(
		@RequestBody CommandUploadFile request,
		@AuthenticationPrincipal CustomMemberDetails member) {
		return ApiResult.success("presigned-uri을 생성하였습니다.", comnFileService.getPreSignedUrl(request));
	}
}
