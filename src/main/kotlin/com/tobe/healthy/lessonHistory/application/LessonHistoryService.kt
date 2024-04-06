package com.tobe.healthy.lessonHistory.application

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.*
import com.tobe.healthy.lessonHistory.domain.dto.RegisterLessonHistoryCommand
import com.tobe.healthy.lessonHistory.domain.entity.LessonHistory
import com.tobe.healthy.lessonHistory.repository.LessonHistoryRepository
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.schedule.repository.ScheduleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LessonHistoryService(
    private val lessonHistoryRepository: LessonHistoryRepository,
    private val memberRepository: MemberRepository,
    private val scheduleRepository: ScheduleRepository
) {

    fun registerLessonHistory(request: RegisterLessonHistoryCommand, studentId: Long): Boolean {
        val findMember = memberRepository.findById(studentId).orElseThrow {
            throw CustomException(MEMBER_NOT_FOUND)
        }

        val findTrainer = memberRepository.findById(request.trainer).orElseThrow {
            throw CustomException(TRAINER_NOT_FOUND)
        }

        val findSchedule = scheduleRepository.findById(request.schedule).orElseThrow {
            throw CustomException(SCHEDULE_NOT_FOUND)
        }

        val lessonHistory = LessonHistory.register(request, findMember, findTrainer, findSchedule)

        lessonHistoryRepository.save(lessonHistory)

        return true
    }
}