package com.tobe.healthy.lessonhistory.repository

import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryFiles
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LessonHistoryFilesRepository : JpaRepository<LessonHistoryFiles, Long>
