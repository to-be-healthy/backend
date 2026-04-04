package com.tobe.healthy.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tobe.healthy.course.domain.CourseHistory;

public interface CourseHistoryRepository extends JpaRepository<CourseHistory, Long>, CourseHistoryRepositoryCustom {

}
