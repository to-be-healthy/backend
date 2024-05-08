package com.tobe.healthy.schedule.application

import com.tobe.healthy.log
import com.tobe.healthy.schedule.entity.`in`.RegisterScheduleRequest
import com.tobe.healthy.schedule.entity.`in`.ScheduleSearchCond
import io.kotest.core.spec.style.BehaviorSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate.of

@SpringBootTest
@Transactional
class TrainerScheduleServiceTest(
    private val trainerScheduleService: TrainerScheduleService,
) : BehaviorSpec({
    val requestSchedule = RegisterScheduleRequest(
        startDt = of(2029, 12, 1),
        endDt = of(2029, 12, 1)
    )

    // 특정 상황 설정
    Given("트레이너가 일정을 등록하고") {
        val trainerId = 542L
        trainerScheduleService.registerSchedule(requestSchedule, trainerId)
        // 특정 상황 실행
        When("등록한 일정을 조회를 했을 때") {
            // 기대하는 결과 확인
            val searchScheduleCond = ScheduleSearchCond(
                lessonDt = "202912"
            )
            val findAllSchedule = trainerScheduleService.findAllSchedule(searchScheduleCond, trainerId)

            Then("트레이너가 일정을 삭제하고 검증한다") {
                for (schedule in findAllSchedule) {
                    log.info { "schedule=> ${schedule}" }
                }
            }
        }
    }
})
