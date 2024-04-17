package com.tobe.healthy.course.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.course.domain.dto.CourseDto;
import com.tobe.healthy.course.domain.dto.CourseHistoryDto;
import com.tobe.healthy.course.domain.dto.in.CourseAddCommand;
import com.tobe.healthy.course.domain.dto.in.CourseUpdateCommand;
import com.tobe.healthy.course.domain.dto.out.CourseGetResult;
import com.tobe.healthy.course.domain.entity.Course;
import com.tobe.healthy.course.domain.entity.CourseHistory;
import com.tobe.healthy.course.domain.entity.CourseHistoryType;
import com.tobe.healthy.course.repository.CourseHistoryRepository;
import com.tobe.healthy.course.repository.CourseRepository;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberType;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.point.domain.entity.Calculation;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.tobe.healthy.config.error.ErrorCode.*;
import static com.tobe.healthy.course.domain.entity.CourseHistoryType.COURSE_CREATE;
import static com.tobe.healthy.member.domain.entity.MemberType.TRAINER;
import static com.tobe.healthy.member.domain.entity.MemberType.STUDENT;
import static com.tobe.healthy.point.domain.entity.Calculation.MINUS;
import static com.tobe.healthy.point.domain.entity.Calculation.PLUS;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;
    private final CourseHistoryRepository courseHistoryRepository;
    private final TrainerMemberMappingRepository mappingRepository;

    public void addCourse(Long trainerId, CourseAddCommand command) {
        Member trainer = memberRepository.findByIdAndMemberTypeAndDelYnFalse(trainerId, TRAINER)
                .orElseThrow(() -> new CustomException(TRAINER_NOT_FOUND));
        Member member = memberRepository.findByIdAndMemberTypeAndDelYnFalse(command.getMemberId(), STUDENT)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        mappingRepository.findByTrainerIdAndMemberId(trainerId, member.getId())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_MAPPED));

        Long cnt = courseRepository.countByMemberIdAndRemainLessonCntGreaterThan(member.getId(), 0);
        if(0 < cnt) throw new CustomException(COURSE_ALREADY_EXISTS);
        Course course = courseRepository.save(Course.create(member, trainer, command.getLessonCnt(), command.getLessonCnt()));
        courseHistoryRepository.save(CourseHistory.create(course, course.getLessonCnt(), PLUS, COURSE_CREATE, trainer));
    }

    public void deleteCourse(Long trainerId, Long courseId) {
        memberRepository.findByIdAndMemberTypeAndDelYnFalse(trainerId, TRAINER)
                .orElseThrow(() -> new CustomException(TRAINER_NOT_FOUND));
        courseRepository.deleteByCourseIdAndTrainerId(courseId, trainerId);
    }

    public CourseGetResult getCourse(Long memberId, Member loginMember, Pageable pageable) {
        memberRepository.findByIdAndMemberTypeAndDelYnFalse(memberId, STUDENT)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        Optional<Course> optCourse = courseRepository.findTop1ByMemberIdAndRemainLessonCntGreaterThanOrderByCreatedAtDesc(memberId, 0);
        CourseDto usingCourse = optCourse.map(CourseDto::from).orElse(null);

        Long trainerId = MemberType.TRAINER.equals(loginMember.getMemberType()) ? loginMember.getId() : null;
        Page<CourseHistory> histories = courseHistoryRepository.getCourseHistory(memberId, trainerId, pageable);
        List<CourseHistoryDto> courseHistoryDtos = histories.map(CourseHistoryDto::from).stream().toList();
        return CourseGetResult.create(usingCourse, courseHistoryDtos);
    }

    public void updateCourse(Long trainerId, Long courseId, CourseUpdateCommand command) {
        Member trainer = memberRepository.findByIdAndMemberTypeAndDelYnFalse(trainerId, TRAINER)
                .orElseThrow(() -> new CustomException(TRAINER_NOT_FOUND));
        Member member = memberRepository.findByIdAndMemberTypeAndDelYnFalse(command.getMemberId(), STUDENT)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        mappingRepository.findByTrainerIdAndMemberId(trainerId, member.getId())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_MAPPED));

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new CustomException(COURSE_NOT_FOUND));
        int result = command.getCalculation().apply(course.getRemainLessonCnt(), command.getUpdateCnt());
        if(result < 0) throw new CustomException(LESSON_CNT_NOT_VALID);
        course.updateLessonCnt(command);
        courseHistoryRepository.save(CourseHistory.create(course, command.getUpdateCnt(), command.getCalculation(), command.getType(), trainer));
    }

}