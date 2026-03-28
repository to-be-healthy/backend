package com.tobe.healthy.lessonhistory.domain.dto.in;

import com.tobe.healthy.lessonhistory.domain.entity.WritingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnwrittenLessonHistorySearchCond {

    private String lessonDate;
    private Long studentId;
    private WritingStatus writingStatus;
}
