package com.tobe.healthy.common.aop;

import com.tobe.healthy.point.application.PointService;
import com.tobe.healthy.point.domain.entity.Calculation;
import com.tobe.healthy.point.domain.entity.PointType;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryDto;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;


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

    @AfterReturning(value = "addWorkoutHistory()", returning = "returnValue")
    public void plusPoint(JoinPoint joinPoint, Object returnValue) {
        Long memberId;
        PointType type;

        switch (joinPoint.getSignature().getName()) {
            case "addWorkoutHistory" -> {
                memberId = ((WorkoutHistoryDto) returnValue).getMember().getId();
                type = PointType.WORKOUT;
            }
//            case "addDiet" -> {
//                //TODO: 식단기록 등록 개발 후 수정필요
//                memberId = ((WorkoutHistoryDto) returnValue).getMember().getId();
//                type = PointType.DIET;
//            }
            default -> throw new IllegalStateException("Unexpected value: " + joinPoint.getSignature().getName());
        }

        //메서드가 호출되는 시점에 스프링 컨테이너에 등록된 Bean을 조회 (지연조회)
        PointService pointService = pointServiceProvider.getObject();
        pointService.updatePoint(memberId, type, Calculation.PLUS, ONE_POINT);
    }

    //TODO: 포인트 차감 메소드 종류 (No Show)
    public void minusPoint(JoinPoint joinPoint, Object returnValue) {
        Long memberId;
        PointType type;

        switch (joinPoint.getSignature().getName()) {
            case "addWorkoutHistory" -> {
                memberId = ((WorkoutHistoryDto) returnValue).getMember().getId();
                type = PointType.WORKOUT;
            }
            default -> throw new IllegalStateException("Unexpected value: " + joinPoint.getSignature().getName());
        }

        //메서드가 호출되는 시점에 스프링 컨테이너에 등록된 Bean을 조회 (지연조회)
        PointService pointService = pointServiceProvider.getObject();
        pointService.updatePoint(memberId, type, Calculation.MINUS, THREE_POINT);
    }

}
