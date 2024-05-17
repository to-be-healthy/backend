package com.tobe.healthy.lessonhistory.application

import com.tobe.healthy.lessonhistory.domain.dto.`in`.CommentRegisterCommand
import com.tobe.healthy.lessonhistory.domain.dto.`in`.RegisterLessonHistoryCommand
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.schedule.repository.trainer.TrainerScheduleRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
class LessonHistoryServiceTest(
    private val memberRepository: MemberRepository,
    private val trainerScheduleRepository: TrainerScheduleRepository,
    private val lessonHistoryService: LessonHistoryService
) : BehaviorSpec({

    Given("수업일지를 작성하기 위한 스케줄을 등록하고") {
        val student = memberRepository.findByUserId("healthy-student0").get()
        val trainer = memberRepository.findByUserId("healthy-trainer0").get()
        val schedule = trainerScheduleRepository.findByIdOrNull(1L)

        When("수업일지를 등록한 뒤에") {
            val request = RegisterLessonHistoryCommand(
                title = "수업일지 테스트 제목",
                content = "수업일지 테스트 내용",
                studentId = student.id,
                scheduleId = schedule!!.id,
                null
            )

            val result = lessonHistoryService.registerLessonHistory(request, trainer.id)

            When("댓글을 등록하고") {
                val commentRequest = CommentRegisterCommand(comment = "수업일지 테스트 댓글", null)
                val response = lessonHistoryService.registerLessonHistoryComment(result.lessonHistoryId, commentRequest, student.id)

                Then("댓글이 정상적으로 등록되었는지 검증한다") {
                    response.comment shouldBe commentRequest.comment
                    response.writerId shouldBe student.id
                }
            }

            Then("수업일지가 정상적으로 등록되었는지 검증한다") {
                request.title shouldBe result.title
                request.content shouldBe result.content
            }
        }
    }
})
