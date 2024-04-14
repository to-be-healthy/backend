package com.tobe.healthy.course.repository;

import com.tobe.healthy.course.domain.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long>, CourseRepositoryCustom {

    Long countByMemberIdAndRemainLessonCntGreaterThan(Long memberId, int remainLessonCnt);
    void deleteByCourseIdAndTrainerId(Long courseId, Long trainerId);
    Optional<Course> findTop1ByMemberIdAndRemainLessonCntGreaterThanOrderByCreatedAtDesc(Long memberId, int i);

}
