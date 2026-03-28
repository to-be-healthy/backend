package com.tobe.healthy.lessonhistory.repository;

import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryComment;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonHistoryCommentRepositoryCustom {
    int findTopComment(Long lessonHistoryId, Long lessonHistoryCommentId);
    LessonHistoryComment findLessonHistoryCommentWithFiles(Long lessonHistoryCommentId, Long writerId);
    LessonHistoryComment findCommentById(Long lessonHistoryCommentId);
    LessonHistoryComment findById(Long lessonHistoryCommentId, Long writerId);
}
