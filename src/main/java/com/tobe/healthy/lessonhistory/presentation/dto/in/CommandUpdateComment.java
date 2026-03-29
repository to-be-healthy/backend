package com.tobe.healthy.lessonhistory.presentation.dto.in;

import java.util.ArrayList;
import java.util.List;

import com.tobe.healthy.lessonhistory.presentation.dto.out.CommandUploadFileResult;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "수업 일지 댓글 수정 DTO")
public class CommandUpdateComment {

	@Schema(description = "수정할 댓글 내용", example = "트레이너님 휴대폰 그만봐", required = true)
	private String content;

	@Schema(description = "등록할 파일", required = false)
	@Builder.Default
	private List<CommandUploadFileResult> uploadFiles = new ArrayList<>();
}
