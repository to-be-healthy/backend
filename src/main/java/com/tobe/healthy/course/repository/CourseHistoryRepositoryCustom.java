package com.tobe.healthy.course.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tobe.healthy.course.domain.CourseHistory;

public interface CourseHistoryRepositoryCustom {
	Page<CourseHistory> getCourseHistory(Long memberId, Long trainerId, Pageable pageable, String searchDate);

	Long checkPaidOneLesson(Long trainerId, String searchDate);

}
