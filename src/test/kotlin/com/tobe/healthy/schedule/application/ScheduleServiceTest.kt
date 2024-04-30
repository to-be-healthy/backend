package com.tobe.healthy.schedule.application

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.SCHEDULE_LESS_THAN_30_DAYS
import com.tobe.healthy.config.error.ErrorCode.START_DATE_AFTER_END_DATE
import com.tobe.healthy.schedule.domain.dto.`in`.AutoCreateScheduleCommand
import com.tobe.healthy.schedule.domain.dto.`in`.RegisterScheduleCommand
import com.tobe.healthy.schedule.domain.dto.`in`.ScheduleSearchCond
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult
import com.tobe.healthy.schedule.domain.entity.LessonTime.ONE_HOUR
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalTime.of

@SpringBootTest
@Transactional
class ScheduleServiceTest @Autowired constructor(
    private val scheduleService: ScheduleService,
) : BehaviorSpec(
    {

        Given("일정을 등록하고") {
            val request = AutoCreateScheduleCommand.builder()
                .startDt(LocalDate.of(2024, 4, 10))
                .endDt(LocalDate.of(2024, 4, 15))
                .startTime(of(10, 0))
                .endTime(of(20, 0))
                .lunchStartTime(LocalTime.of(12, 0))
                .lunchEndTime(of(13, 0))
                .closedDt(LocalDate.of(2024, 4, 15))
                .sessionTime(ONE_HOUR)
                .build()
            When("휴무일에 추가 일정을 등록하고") {
                val request = RegisterScheduleCommand.builder()
                    .lessonDt(LocalDate.of(2024, 4, 15))
                    .lessonStartTime(of(10, 0))
                    .lessonEndTime(of(20, 0))
                    .build()
                scheduleService.registerIndividualSchedule(request, 741L)
                Then("정상적으로 등록됐는지 검증한다") {
                    val request = ScheduleSearchCond.builder()
                        .lessonStartDt(LocalDate.of(2024, 4, 15))
                        .lessonEndDt(LocalDate.of(2024, 4, 15))
                        .build()
                    scheduleService.findAllSchedule(request, null) shouldNotBe emptyList<ScheduleCommandResult>()
                }
            }
            Then("예외가 발생한다") {
                val exception = shouldThrow<CustomException> {
                    scheduleService.registerSchedule(request, 741L)
                }
                exception.message shouldBe START_DATE_AFTER_END_DATE.message
            }
        }

        Given("30일 이상 일정을 등록하고") {
            val request = AutoCreateScheduleCommand.builder()
                .startDt(LocalDate.of(2024, 4, 10))
                .endDt(LocalDate.of(2024, 5, 30))
                .startTime(of(10, 0))
                .endTime(of(20, 0))
                .lunchStartTime(LocalTime.of(12, 0))
                .lunchEndTime(of(13, 0))
                .closedDt(LocalDate.of(2024, 4, 15))
                .sessionTime(ONE_HOUR)
                .build()
            Then("일정 등록 최대 일수가 넘어 예외가 발생한다") {
                val exception = shouldThrow<CustomException> {
                    scheduleService.registerSchedule(request, 741L)
                }
                exception.message shouldBe SCHEDULE_LESS_THAN_30_DAYS.message
            }
        }

        Given("등록할 시작 일정이 종료 일정보다 크다면") {
            val request = AutoCreateScheduleCommand.builder()
                .startDt(LocalDate.of(2024, 5, 10))
                .endDt(LocalDate.of(2024, 4, 28))
                .startTime(of(10, 0))
                .endTime(of(20, 0))
                .lunchStartTime(LocalTime.of(12, 0))
                .lunchEndTime(of(13, 0))
                .closedDt(LocalDate.of(2024, 4, 15))
                .sessionTime(ONE_HOUR)
                .build()
            Then("예외가 발생한다") {
                val exception = shouldThrow<CustomException> {
                    scheduleService.registerSchedule(request, 741L)
                }
                exception.message shouldBe START_DATE_AFTER_END_DATE.message
            }
        }
    }
)
