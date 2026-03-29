package com.tobe.healthy.lessonhistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tobe.healthy.lessonhistory.domain.entity.LessonHistory;

public interface LessonHistoryRepository extends JpaRepository<LessonHistory, Long>, LessonHistoryRepositoryCustom {
	LessonHistory findByIdAndTrainerId(Long lessonHistoryId, Long trainerId);
}
