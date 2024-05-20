package com.tobe.healthy.schedule.repository.student;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.schedule.domain.dto.in.RetrieveTrainerScheduleByLessonInfo;
import com.tobe.healthy.schedule.domain.dto.out.MyReservation;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;

import java.util.List;

public interface StudentScheduleRepositoryCustom {
	List<ScheduleCommandResult> findAllSchedule(RetrieveTrainerScheduleByLessonInfo searchCond, Long trainerId, Member member);
	List<ScheduleCommandResult> findAllByApplicantId(Long memberId);
	List<MyReservation> findAllMyReservation(Long memberId, RetrieveTrainerScheduleByLessonInfo searchCond);
	MyReservation findMyNextReservation(Long memberId);

}
