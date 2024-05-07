package com.tobe.healthy.common.aop;

import static com.tobe.healthy.course.domain.entity.CourseHistoryType.RESERVATION;
import static com.tobe.healthy.course.domain.entity.CourseHistoryType.RESERVATION_CANCEL;
import static com.tobe.healthy.point.domain.entity.Calculation.MINUS;
import static com.tobe.healthy.point.domain.entity.Calculation.PLUS;

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


@Slf4j
@Aspect
@Component
public class CourseAspect {

    private final int ONE_LESSON = 1;

    private final ObjectProvider<CourseService> courseServiceObjectProvider;

    public CourseAspect(ObjectProvider<CourseService> courseServiceObjectProvider) {
        this.courseServiceObjectProvider = courseServiceObjectProvider;
    }

    @Pointcut("execution(* com.tobe.healthy.schedule.application.TrainerScheduleService.cancelMemberSchedule(..))")
    private void cancelMemberSchedule() {}

    @Pointcut("execution(* com.tobe.healthy.schedule.application.TrainerScheduleService.reserveSchedule(..))")
    private void reserveSchedule() {}

    @AfterReturning(value = "cancelMemberSchedule()", returning = "returnValue")
    public void updateCourseWhenCancelSchedule(JoinPoint joinPoint, Object returnValue) {
        Long studentId = ((ScheduleIdInfo) returnValue).getStudentId();
        Long waitingStudentId = ((ScheduleIdInfo) returnValue).getWaitingStudentId();
        Long trainerId = ((ScheduleIdInfo) returnValue).getTrainerId();
        CourseUpdateCommand command;

        //수업 취소자 수강권 +1
        CourseService courseService = courseServiceObjectProvider.getObject();
        command = CourseUpdateCommand.create(studentId, PLUS, RESERVATION_CANCEL, ONE_LESSON);
        courseService.updateCourse(trainerId, null, command);

        //수업 대기자 수강권 -1
        if(!ObjectUtils.isEmpty(waitingStudentId)){
            command = CourseUpdateCommand.create(waitingStudentId, MINUS, RESERVATION, ONE_LESSON);
            courseService.updateCourse(trainerId, null, command);
        }
    }

    @AfterReturning(value = "reserveSchedule()", returning = "returnValue")
    public void minusCourseWhenReserveSchedule(JoinPoint joinPoint, Object returnValue) {
        Long studentId = ((ScheduleIdInfo) returnValue).getStudentId();
        Long trainerId = ((ScheduleIdInfo) returnValue).getTrainerId();

        CourseService courseService = courseServiceObjectProvider.getObject();
        CourseUpdateCommand command = CourseUpdateCommand.create(studentId, MINUS, RESERVATION, ONE_LESSON);
        courseService.updateCourse(trainerId, null, command);
    }

}
