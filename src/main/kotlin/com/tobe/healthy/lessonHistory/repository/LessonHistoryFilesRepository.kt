package com.tobe.healthy.lessonHistory.repository

import com.tobe.healthy.lessonHistory.domain.entity.LessonHistoryFiles
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LessonHistoryFilesRepository : JpaRepository<LessonHistoryFiles, Long>
