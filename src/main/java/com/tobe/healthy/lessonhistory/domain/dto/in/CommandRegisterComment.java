package com.tobe.healthy.lessonhistory.domain.dto.in;

import com.tobe.healthy.lessonhistory.domain.dto.out.CommandUploadFileResult;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "댓글 등록 DTO")
public class CommandRegisterComment {

    @Schema(description = "등록할 댓글 내용")
    @NotBlank(message = "내용을 입력해 주세요.")
    private String content;

    @Schema(description = "등록할 파일", required = false)
    @Builder.Default
    private List<CommandUploadFileResult> uploadFiles = new ArrayList<>();
}
