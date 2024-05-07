package com.tobe.healthy.course.repository;

import com.tobe.healthy.course.domain.entity.Course;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Long countByMemberIdAndRemainLessonCntGreaterThan(Long memberId, int remainLessonCnt);
    void deleteByCourseIdAndTrainerId(Long courseId, Long trainerId);
    Optional<Course> findTop1ByMemberIdAndRemainLessonCntGreaterThanOrderByCreatedAtDesc(Long memberId, int i);
    Optional<Course> findByCourseIdAndMemberIdAndTrainerId(Long courseId, Long memberId, Long trainerId);

}
