package com.tobe.healthy.course.repository;

import com.tobe.healthy.course.domain.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Long countByMemberIdAndTrainerIdAndRemainLessonCntGreaterThan(Long memberId, Long trainerId, int remainLessonCnt);

}
