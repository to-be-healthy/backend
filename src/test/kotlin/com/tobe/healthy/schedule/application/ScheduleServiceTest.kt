package com.tobe.healthy.schedule.application

import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.schedule.domain.dto.`in`.RegisterScheduleRequest
import com.tobe.healthy.schedule.domain.dto.`in`.ScheduleSearchCond
import com.tobe.healthy.schedule.domain.entity.LessonTime.ONE_HOUR
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@SpringBootTest
@Transactional
class ScheduleServiceTest @Autowired constructor(
    private val trainerScheduleService: TrainerScheduleService,
    private val memberRepository: MemberRepository
) : BehaviorSpec({
        Given("일정을 등록하고") {
            val trainerId = 746L
            val request = RegisterScheduleRequest.builder()
                .startDt(LocalDate.of(2021, 10, 1))
                .endDt(LocalDate.of(2021, 10, 3))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(18, 0))
                .sessionTime(ONE_HOUR)
                .closedDt(listOf(LocalDate.of(2021, 10, 1), LocalDate.of(2021, 10, 2)))
                .build()
            trainerScheduleService.registerSchedule(request, trainerId)
            When("일정을 조회하면") {
                val cond = ScheduleSearchCond.builder()
                    .lessonStartDt(LocalDate.of(2021, 10, 1))
                    .lessonEndDt(LocalDate.of(2021, 10, 4))
                    .build()
                val trainer = memberRepository.findByIdOrNull(trainerId)
                val results = trainerScheduleService.findAllSchedule(cond, trainer!!)
                Then("일정이 등록되어 있다.") {
                    results.size shouldBe 10
                }
            }
        }
    }
)
