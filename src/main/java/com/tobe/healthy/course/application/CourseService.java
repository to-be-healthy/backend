package com.tobe.healthy.course.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.course.domain.dto.in.CourseAddCommand;
import com.tobe.healthy.course.domain.entity.Course;
import com.tobe.healthy.course.repository.CourseRepository;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.tobe.healthy.config.error.ErrorCode.*;
import static com.tobe.healthy.member.domain.entity.MemberType.TRAINER;
import static com.tobe.healthy.member.domain.entity.MemberType.STUDENT;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;
    private final TrainerMemberMappingRepository mappingRepository;

    public void addCourse(Member trainer, CourseAddCommand command) {
        memberRepository.findByIdAndMemberTypeAndDelYnFalse(trainer.getId(), TRAINER)
                .orElseThrow(() -> new CustomException(TRAINER_NOT_FOUND));
        Member member = memberRepository.findByIdAndMemberTypeAndDelYnFalse(command.getMemberId(), STUDENT)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        mappingRepository.findByTrainerIdAndMemberId(trainer.getId(), member.getId())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_MAPPED));

        Long cnt = courseRepository.countByMemberIdAndTrainerIdAndRemainLessonCntGreaterThan(member.getId(), trainer.getId(), 0);
        if(0 < cnt) throw new CustomException(COURSE_ALREADY_EXISTS);
        courseRepository.save(Course.create(member, trainer, command.getLessonCnt(), command.getLessonCnt()));
    }

}
