package com.tobe.healthy.schedule.repository.common;


public interface CommonScheduleRepositoryCustom {
    Long getCompletedLessonCnt(Long memberId, Long courseId);
}
