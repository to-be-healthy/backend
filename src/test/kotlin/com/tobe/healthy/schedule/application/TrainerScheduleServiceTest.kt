package com.tobe.healthy.schedule.application

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.SCHEDULE_ALREADY_EXISTS
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.schedule.entity.`in`.RegisterScheduleCommand
import com.tobe.healthy.schedule.entity.`in`.RegisterScheduleRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@SpringBootTest
@Transactional
class TrainerScheduleServiceTest(
    private val trainerScheduleService: TrainerScheduleService,
    private val memberRepository: MemberRepository,
) : BehaviorSpec({
    Given("트레이너가 등록할 일정 정보를 설정하고") {
        val trainer = memberRepository.findByUserId("healthy-trainer0").get()

        val request = RegisterScheduleRequest(
            lessonStartDt = LocalDate.of(2024, 5, 1),
            lessonEndDt = LocalDate.of(2024, 5, 30),
        )

        When("일정을 등록했을 때") {
            val result = trainerScheduleService.registerSchedule(request, trainer.id)

            Then("등록된 일정이 있어야 한다") {
                result.lessonDt.size shouldBe 30
                result.lessonStartTime shouldBe LocalTime.of(9, 0, 0)
                result.lessonEndTime shouldBe LocalTime.of(20, 0, 0)
            }

            Then("이미 등록한 일정이 있으면 예외가 발생해야 한다") {
                val request = RegisterScheduleCommand(
                    lessonDt = LocalDate.of(2024, 5, 1),
                    lessonStartTime = LocalTime.of(8, 0),
                    lessonEndTime = LocalTime.of(9, 10),
                )
                val message = shouldThrow<CustomException> {
                    trainerScheduleService.registerIndividualSchedule(request, trainer.id)
                }.message

                message shouldBe SCHEDULE_ALREADY_EXISTS.message
            }
        }
    }
})
