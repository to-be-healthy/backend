package com.tobe.healthy.lessonhistory.domain.dto.out;

import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryComment;
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryFiles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandRegisterReplyResult {

    private Long lessonHistoryId;
    private Long commentId;
    private String content;
    @Builder.Default
    private List<CommandUploadFileResult> files = new ArrayList<>();
    private int order;
    private boolean delYn;
    private Long parentId;

    public static CommandRegisterReplyResult from(LessonHistoryComment lessonHistoryComment, List<LessonHistoryFiles> files) {
        return CommandRegisterReplyResult.builder()
                .lessonHistoryId(lessonHistoryComment.getLessonHistory() != null ? lessonHistoryComment.getLessonHistory().getId() : null)
                .commentId(lessonHistoryComment.getId())
                .content(lessonHistoryComment.getContent())
                .files(files.stream().map(CommandUploadFileResult::from).collect(Collectors.toList()))
                .order(lessonHistoryComment.getOrder())
                .delYn(lessonHistoryComment.isDelYn())
                .parentId(lessonHistoryComment.getParent() != null ? lessonHistoryComment.getParent().getId() : null)
                .build();
    }
}
