package com.tobe.healthy.course.repository;

import com.tobe.healthy.course.domain.entity.CourseHistory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CourseHistoryRepository extends JpaRepository<CourseHistory, Long>, CourseHistoryRepositoryCustom {

}
