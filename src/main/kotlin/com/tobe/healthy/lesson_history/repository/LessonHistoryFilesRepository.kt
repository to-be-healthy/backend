package com.tobe.healthy.lesson_history.repository

import com.tobe.healthy.lesson_history.domain.entity.LessonHistoryFiles
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LessonHistoryFilesRepository : JpaRepository<LessonHistoryFiles, Long>
