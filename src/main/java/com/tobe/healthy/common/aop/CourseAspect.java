package com.tobe.healthy.common.aop;

import com.tobe.healthy.course.application.CourseService;
import com.tobe.healthy.course.domain.dto.in.CourseUpdateCommand;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleIdInfo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import static com.tobe.healthy.course.domain.entity.CourseHistoryType.RESERVATION;
import static com.tobe.healthy.course.domain.entity.CourseHistoryType.RESERVATION_CANCEL;
import static com.tobe.healthy.point.domain.entity.Calculation.MINUS;
import static com.tobe.healthy.point.domain.entity.Calculation.PLUS;


@Slf4j
@Aspect
@Component
public class CourseAspect {

    private final int ONE_LESSON = 1;

    private final ObjectProvider<CourseService> courseServiceObjectProvider;

    public CourseAspect(ObjectProvider<CourseService> courseServiceObjectProvider) {
        this.courseServiceObjectProvider = courseServiceObjectProvider;
    }

    @Pointcut("execution(* com.tobe.healthy.schedule.application.CommonScheduleService.cancelMemberSchedule(..))")
    private void cancelMemberSchedule() {}

    @Pointcut("execution(* com.tobe.healthy.schedule.application.CommonScheduleService.reserveSchedule(..))")
    private void reserveSchedule() {}

    /*
    * 수업 취소
    */
    @AfterReturning(value = "cancelMemberSchedule()", returning = "returnValue")
    public void updateCourseWhenCancelSchedule(JoinPoint joinPoint, Object returnValue) {
        ScheduleIdInfo scheduleIdInfo = ((ScheduleIdInfo) returnValue);
        Long scheduleId = scheduleIdInfo.getScheduleId();
        Long studentId = scheduleIdInfo.getStudentId();
        Long waitingStudentId = scheduleIdInfo.getWaitingStudentId();
        Long trainerId = scheduleIdInfo.getTrainerId();
        CourseUpdateCommand command;

        //수업 취소자 수강권 +1
        CourseService courseService = courseServiceObjectProvider.getObject();
        command = CourseUpdateCommand.create(studentId, PLUS, RESERVATION_CANCEL, ONE_LESSON);
        courseService.updateCourseByMember(scheduleId, trainerId, command);

        //수업 대기자 수강권 -1
        if(!ObjectUtils.isEmpty(waitingStudentId)){
            command = CourseUpdateCommand.create(waitingStudentId, MINUS, RESERVATION, ONE_LESSON);
            courseService.updateCourseByMember(scheduleId, trainerId, command);
        }
    }

    /*
     * 수업 예약
     */
    @AfterReturning(value = "reserveSchedule()", returning = "returnValue")
    public void minusCourseWhenReserveSchedule(JoinPoint joinPoint, Object returnValue) {
        ScheduleIdInfo scheduleIdInfo = ((ScheduleIdInfo) returnValue);
        Long scheduleId = scheduleIdInfo.getScheduleId();
        Long studentId = scheduleIdInfo.getStudentId();
        Long trainerId = scheduleIdInfo.getTrainerId();

        CourseService courseService = courseServiceObjectProvider.getObject();
        CourseUpdateCommand command = CourseUpdateCommand.create(studentId, MINUS, RESERVATION, ONE_LESSON);
        courseService.updateCourseByMember(scheduleId, trainerId, command);
    }

}
