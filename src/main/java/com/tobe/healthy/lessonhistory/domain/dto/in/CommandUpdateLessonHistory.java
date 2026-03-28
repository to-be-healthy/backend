package com.tobe.healthy.lessonhistory.domain.dto.in;

import com.tobe.healthy.lessonhistory.domain.dto.out.CommandUploadFileResult;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class CommandUpdateLessonHistory {

    @Schema(description = "수정할 수업일지 제목")
    private String title;

    @Schema(description = "수정할 수업일지 내용")
    private String content;

    @Schema(description = "등록할 파일", required = false)
    @Builder.Default
    private List<CommandUploadFileResult> uploadFiles = new ArrayList<>();
}
