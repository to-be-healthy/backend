package com.tobe.healthy.lessonhistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryComment;

@Repository
public interface LessonHistoryCommentRepository extends JpaRepository<LessonHistoryComment, Long>,
	LessonHistoryCommentRepositoryCustom {
}
