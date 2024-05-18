package com.tobe.healthy.schedule.repository;


public interface CommonScheduleRepositoryCustom {
    Long getCompletedLessonCnt(Long memberId, Long courseId);
}
