package com.tobe.healthy.course.repository;


import com.tobe.healthy.course.domain.entity.CourseHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CourseHistoryRepositoryCustom {

    Page<CourseHistory> getCourseHistory(Long memberId, Long trainerId, Pageable pageable, String searchDate);

}
