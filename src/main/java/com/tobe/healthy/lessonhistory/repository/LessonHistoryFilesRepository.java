package com.tobe.healthy.lessonhistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tobe.healthy.lessonhistory.domain.LessonHistoryFiles;

@Repository
public interface LessonHistoryFilesRepository extends JpaRepository<LessonHistoryFiles, Long> {
}
