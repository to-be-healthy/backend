package com.tobe.healthy.common.aop;

import com.tobe.healthy.diet.domain.dto.DietDto;
import com.tobe.healthy.point.application.PointService;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleIdInfo;
import com.tobe.healthy.workout.domain.dto.out.WorkoutHistoryDto;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import static com.tobe.healthy.point.domain.entity.Calculation.MINUS;
import static com.tobe.healthy.point.domain.entity.Calculation.PLUS;
import static com.tobe.healthy.point.domain.entity.PointType.*;


@Slf4j
@Aspect
@Component
public class PointAspect {

    private final int ONE_POINT = 1;
    private final int THREE_POINT = 3;

    private final ObjectProvider<PointService> pointServiceProvider;

    public PointAspect(ObjectProvider<PointService> pointServiceProvider) {
        this.pointServiceProvider = pointServiceProvider;
    }

    @Pointcut("execution(* com.tobe.healthy.workout.application.WorkoutHistoryService.addWorkoutHistory(..))")
    private void addWorkoutHistory() {}

    @Pointcut("execution(* com.tobe.healthy.diet.application.DietService.addDiet(..))")
    private void addDiet() {}

    @Pointcut("execution(* com.tobe.healthy.schedule.application.TrainerScheduleService.updateReservationStatusToNoShow(..))")
    private void updateReservationStatusToNoShow() {}

    @Pointcut("execution(* com.tobe.healthy.schedule.application.TrainerScheduleService.revertReservationStatusToNoShow(..))")
    private void revertReservationStatusToNoShow() {}

    /*
     * 운동기록 등록
     */
    @AfterReturning(value = "addWorkoutHistory()", returning = "returnValue")
    public void plusPointWhenPostWorkout(JoinPoint joinPoint, Object returnValue) {
        Long memberId = ((WorkoutHistoryDto) returnValue).getMember().getId();
        //메서드가 호출되는 시점에 스프링 컨테이너에 등록된 Bean을 조회 (지연조회)
        PointService pointService = pointServiceProvider.getObject();
        pointService.updatePoint(memberId, WORKOUT, PLUS, ONE_POINT);
    }

    /*
     * 식단기록 등록
     */
    @AfterReturning(value = "addDiet()", returning = "returnValue")
    public void plusPointWhenPostDiet(JoinPoint joinPoint, Object returnValue) {
        Long memberId = ((DietDto) returnValue).getMember().getId();
        PointService pointService = pointServiceProvider.getObject();
        pointService.updatePoint(memberId, DIET, PLUS, ONE_POINT);
    }

    /*
     * 노쇼 처리
     */
    @AfterReturning(value = "updateReservationStatusToNoShow()", returning = "returnValue")
    public void minusPointWhenNoShow(JoinPoint joinPoint, Object returnValue) {
        Long memberId = ((ScheduleIdInfo) returnValue).getStudentId();
        PointService pointService = pointServiceProvider.getObject();
        pointService.updatePoint(memberId, NO_SHOW, MINUS, THREE_POINT);
    }

    /*
     * 노쇼 취소
     */
    @AfterReturning(value = "revertReservationStatusToNoShow()", returning = "returnValue")
    public void plusPointWhenRevertNoShow(JoinPoint joinPoint, Object returnValue) {
        Long memberId = ((ScheduleIdInfo) returnValue).getStudentId();
        PointService pointService = pointServiceProvider.getObject();
        pointService.updatePoint(memberId, NO_SHOW_CANCEL, PLUS, THREE_POINT);
    }

}
