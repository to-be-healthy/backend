//package com.tobe.healthy.schedule.application
//
//import com.tobe.healthy.config.error.CustomException
//import com.tobe.healthy.config.error.ErrorCode.LUNCH_TIME_INVALID
//import com.tobe.healthy.config.error.ErrorCode.START_TIME_AFTER_END_TIME
//import com.tobe.healthy.log
//import com.tobe.healthy.schedule.domain.entity.LessonTime
//import com.tobe.healthy.schedule.entity.`in`.RegisterDefaultLessonTimeRequest
//import io.kotest.assertions.throwables.shouldThrow
//import io.kotest.core.spec.style.StringSpec
//import io.kotest.matchers.shouldBe
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.transaction.annotation.Transactional
//import java.time.DayOfWeek.FRIDAY
//import java.time.LocalTime
//
//@SpringBootTest
//@Transactional
//class TrainerScheduleServiceTest(
//    private val trainerScheduleService: TrainerScheduleService
//) : StringSpec({
//    val trainerId = 542L
//
//    "트레이너의 기본 수업 시간을 설정할 때 시작 또는 종료 점심시간이 같으면 예외를 발생시킨다" {
//        val exception = shouldThrow<CustomException> {
//            RegisterDefaultLessonTimeRequest(
//                startTime = LocalTime.of(10, 0, 0),
//                endTime = LocalTime.of(12, 0, 0),
//                lunchStartTime = LocalTime.of(10, 0, 0),
//                lunchEndTime = LocalTime.of(10, 0, 0),
//                closedDt = listOf(FRIDAY),
//                sessionTime = LessonTime.ONE_HOUR
//            )
//        }
//        log.info { "exception message: ${LUNCH_TIME_INVALID.message}" }
//        exception.message shouldBe LUNCH_TIME_INVALID.message
//    }
//
//    "트레이너의 기본 수업 시간을 설정할 때 시작 또는 종료 수업시간이 같으면 예외를 발생시킨다" {
//        val exception = shouldThrow<CustomException> {
//            RegisterDefaultLessonTimeRequest(
//                startTime = LocalTime.of(11, 0, 0),
//                endTime = LocalTime.of(10, 0, 0),
//                lunchStartTime = LocalTime.of(11, 0, 0),
//                lunchEndTime = LocalTime.of(12, 0, 0),
//                closedDt = listOf(FRIDAY),
//                sessionTime = LessonTime.ONE_HOUR
//            )
//        }
//        log.info { "exception message: ${START_TIME_AFTER_END_TIME.message}" }
//        exception.message shouldBe START_TIME_AFTER_END_TIME.message
//    }
//
//    "트레이너의 기본 수업 시간을 점심시간 없이 등록한다" {
//        val request = RegisterDefaultLessonTimeRequest(
//            startTime = LocalTime.of(8, 0, 0),
//            endTime = LocalTime.of(10, 0, 0),
//            closedDt = listOf(FRIDAY),
//            sessionTime = LessonTime.ONE_HOUR
//        )
//        trainerScheduleService.registerDefaultLessonTime(request, trainerId)
//    }
//})
