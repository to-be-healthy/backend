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
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.point.domain.entity.Calculation;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.repository.CommonScheduleRepository;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
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
import static com.tobe.healthy.member.domain.entity.MemberType.STUDENT;
import static com.tobe.healthy.member.domain.entity.MemberType.TRAINER;
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
    private final CommonScheduleRepository commonScheduleRepository;

    public void addCourse(Long trainerId, CourseAddCommand command) {
        Member trainer = memberRepository.findByIdAndMemberTypeAndDelYnFalse(trainerId, TRAINER)
                .orElseThrow(() -> new CustomException(TRAINER_NOT_FOUND));
        Member member = memberRepository.findByIdAndMemberTypeAndDelYnFalse(command.getMemberId(), STUDENT)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        mappingRepository.findByTrainerIdAndMemberId(trainerId, member.getId())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_MAPPED));

        checkCourAlreadyExists(member.getId());
        Course course = courseRepository.save(Course.create(member, trainer, command.getLessonCnt(), command.getLessonCnt()));
        courseHistoryRepository.save(CourseHistory.create(course, course.getTotalLessonCnt(), PLUS, COURSE_CREATE, trainer));
    }

    private void checkCourAlreadyExists(Long memberId) {
        Long cnt = courseRepository.countByMemberIdAndRemainLessonCntGreaterThan(memberId, 0);
        if(0 < cnt) throw new CustomException(COURSE_ALREADY_EXISTS);
    }

    public void deleteCourse(Long trainerId, Long courseId) {
        memberRepository.findByIdAndMemberTypeAndDelYnFalse(trainerId, TRAINER)
                .orElseThrow(() -> new CustomException(TRAINER_NOT_FOUND));
        courseRepository.deleteByCourseIdAndTrainerId(courseId, trainerId);
    }

    public CourseGetResult getCourse(Member loginMember, Pageable pageable, Long memberId, String searchDate) {
        memberRepository.findByIdAndMemberTypeAndDelYnFalse(memberId, STUDENT)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        Long trainerId = TRAINER.equals(loginMember.getMemberType()) ? loginMember.getId() : null;
        Page<CourseHistory> histories = courseHistoryRepository.getCourseHistory(memberId, trainerId, pageable, searchDate);
        List<CourseHistoryDto> courseHistoryDtos = histories.map(CourseHistoryDto::from).stream().toList();
        Member member = memberRepository.findByMemberIdWithGym(memberId);
        return CourseGetResult.create(getNowUsingCourse(memberId), courseHistoryDtos.isEmpty() ? null : courseHistoryDtos, member.getGym().getName());
    }

    public CourseDto getNowUsingCourse(Long memberId){
        Optional<Course> optCourse = courseRepository.findTop1ByMemberIdAndRemainLessonCntGreaterThanOrderByCreatedAtDesc(memberId, -1);
        if(optCourse.isPresent()){
            CourseDto courseDto = CourseDto.from(optCourse.get());
            Long completedLessonCnt = commonScheduleRepository.getCompletedLessonCnt(memberId, courseDto.getCourseId());
            courseDto.setCompletedLessonCnt(completedLessonCnt.intValue());
            return courseDto;
        }
        return null;
    }

    public void updateCourseByTrainer(Long trainerId, Long courseId, CourseUpdateCommand command) {
        Member trainer = memberRepository.findByIdAndMemberTypeAndDelYnFalse(trainerId, TRAINER)
                .orElseThrow(() -> new CustomException(TRAINER_NOT_FOUND));
        Member member = memberRepository.findByIdAndMemberTypeAndDelYnFalse(command.getMemberId(), STUDENT)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        mappingRepository.findByTrainerIdAndMemberId(trainerId, member.getId())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_MAPPED));
        Course course = courseRepository.findByCourseIdAndMemberIdAndTrainerId(courseId, member.getId(), trainerId)
                    .orElseThrow(() -> new CustomException(COURSE_NOT_FOUND));
        updateCourse(command, trainer, course);
    }

    public void updateCourseByMember(Long scheduleId, Long trainerId, CourseUpdateCommand command) {
        Member trainer = memberRepository.findByIdAndMemberTypeAndDelYnFalse(trainerId, TRAINER)
                .orElseThrow(() -> new CustomException(TRAINER_NOT_FOUND));
        Member member = memberRepository.findByIdAndMemberTypeAndDelYnFalse(command.getMemberId(), STUDENT)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        mappingRepository.findByTrainerIdAndMemberId(trainerId, member.getId())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_MAPPED));
        Course course = courseRepository.findTop1ByMemberIdAndRemainLessonCntGreaterThanOrderByCreatedAtDesc(member.getId(), 0)
                    .orElseThrow(() -> new CustomException(LESSON_CNT_NOT_VALID));

        updateCourse(command, trainer, course);
        updateScheduleCourse(scheduleId, course, command.getCalculation());
    }

    private void updateCourse(CourseUpdateCommand command, Member trainer, Course course) {
        int result = command.getCalculation().apply(course.getRemainLessonCnt(), command.getUpdateCnt());
        if (result < 0) throw new CustomException(LESSON_CNT_NOT_VALID);

        course.updateRemainLessonCnt(command);
        //수강권 변경 주체가 트레이너인 경우 -> 총 횟수도 함께 업데이트
        if (CourseHistoryType.getEnumByGroup(TRAINER).contains(command.getType())) {
            course.updateTotalLessonCnt(command);
        }
        courseHistoryRepository.save(CourseHistory.create(course, command.getUpdateCnt(), command.getCalculation(), command.getType(), trainer));
    }

    private void updateScheduleCourse(Long scheduleId, Course course, Calculation calculation) {
        Schedule schedule = commonScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));
        switch (calculation){
            case PLUS -> schedule.deleteCourse(); //수업취소
            case MINUS -> schedule.registerCourse(course); //수업예약
        }
    }
}