package com.tobe.healthy.lessonhistory.repository;

import com.tobe.healthy.lessonhistory.domain.entity.LessonHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonHistoryRepository extends JpaRepository<LessonHistory, Long>, LessonHistoryRepositoryCustom {
    LessonHistory findByIdAndTrainerId(Long lessonHistoryId, Long trainerId);
}
