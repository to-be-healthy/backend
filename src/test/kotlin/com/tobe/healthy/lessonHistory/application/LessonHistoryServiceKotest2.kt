package com.tobe.healthy.lessonHistory.application

import com.tobe.healthy.lessonHistory.domain.entity.LessonHistory
import com.tobe.healthy.member.application.MemberService
import com.tobe.healthy.member.domain.dto.`in`.MemberJoinCommand
import com.tobe.healthy.member.domain.dto.`in`.MemberLoginCommand
import com.tobe.healthy.member.domain.dto.`in`.MemberPasswordChangeCommand
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.member.domain.entity.MemberType
import com.tobe.healthy.member.domain.entity.MemberType.STUDENT
import com.tobe.healthy.member.domain.entity.MemberType.TRAINER
import com.tobe.healthy.schedule.domain.entity.ReservationStatus.COMPLETED
import com.tobe.healthy.schedule.domain.entity.Schedule
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringTestExtension
import io.kotest.extensions.spring.SpringTestLifecycleMode
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@Transactional
@SpringBootTest
class LessonHistoryServiceKotest2(
    private val memberService: MemberService,
    private val em: EntityManager
) : BehaviorSpec({
    extensions(SpringTestExtension(SpringTestLifecycleMode.Root)) // https://kth990303.tistory.com/374

    Given("회원 가입을 하고") {
        val member = MemberJoinCommand.builder()
            .userId("laborlawseon2")
            .email("laborlawseon2@gmail.com")
            .password("123456789")
            .passwordConfirm("123456789")
            .name("정선우")
            .memberType(STUDENT)
            .build()
        val joinMember = memberService.joinMember(member)

        When("로그인을 할 경우") {
            Then("토큰을 반환한다") {
                val login = MemberLoginCommand("laborlawseon2", "123456789", MemberType.STUDENT)
                val tokens = memberService.login(login)
                tokens.userId shouldBe "laborlawseon2"
            }
        }

        When("비밀번호를 변경하고") {
            val request = MemberPasswordChangeCommand("123456789", "123456789", "987654321")
            Then("비밀번호가 변경되었는지 검증한다") {
                val result = memberService.changePassword(request, joinMember.id)
                result shouldBe true
            }
        }
    }
//
//    "수업 내역에 댓글을 작성한다" {
//        val student = createStudent(passwordEncoder.encode("pass123"))
//        val trainer = createTrainer(passwordEncoder.encode("pass123"))
//        val schedule = createSchedule(trainer, student)
//        val lessonHistory = createLessonHistory("테스트 게시글 제목", "테스트 게시글 내용", trainer, student, schedule)
////        createLessonHistoryComment()
//        em.persist(student)
//        em.persist(trainer)
//        em.persist(schedule)
//        em.persist(lessonHistory)
//        lessonHistory.content shouldBe "테스트 게시글 내용"
//    }
//})
//{
//    companion object {
//        private fun createLessonHistoryComment(lessonHistory: LessonHistory): LessonHistoryComment {
//        return LessonHistoryComment(
//            order = 1,
//            content = "테스트용 댓글 등록",
//            writer = lessonHistory.student,
//            lessonHistory = lessonHistory)
//        }
//
//        private fun createLessonHistory(title: String, content: String, trainer: Member, student: Member, schedule: Schedule) : LessonHistory {
//            return LessonHistory(
//                title,
//                content,
//                mutableListOf(),
//                mutableListOf(),
//                trainer,
//                student,
//                schedule
//            )
//        }
//
})
{
    companion object {
        fun createSchedule(trainer: Member, student: Member): Schedule {
            return Schedule.builder()
                .lessonDt(LocalDate.of(2024, 4, 1))
                .lessonStartTime(LocalTime.of(10, 0))
                .lessonEndTime(LocalTime.of(11, 0))
                .reservationStatus(COMPLETED)
                .round(1)
                .trainer(trainer)
                .applicant(student)
                .build()
        }

        fun createStudent(password: String): Member {
            return Member.builder()
                .userId("student")
                .email("student@gmail.com")
                .password(password)
                .name("student")
                .memberType(STUDENT)
                .build()
        }

        fun createTrainer(password: String): Member {
            return Member.builder()
                .userId("trainer")
                .email("trainer@gmail.com")
                .password(password)
                .name("trainer")
                .memberType(TRAINER)
                .build()
        }
        fun createLessonHistory(title: String, content: String, trainer: Member, student: Member, schedule: Schedule) : LessonHistory {
            return LessonHistory(
                title,
                content,
                mutableListOf(),
                mutableListOf(),
                trainer,
                student,
                schedule
            )
        }
    }
}
