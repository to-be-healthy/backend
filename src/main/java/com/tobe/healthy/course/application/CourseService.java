package com.tobe.healthy.course.application;

import com.tobe.healthy.common.CustomPaging;
import com.tobe.healthy.common.error.CustomException;
import com.tobe.healthy.course.domain.dto.CourseDto;
import com.tobe.healthy.course.domain.dto.CourseHistoryDto;
import com.tobe.healthy.course.domain.dto.CourseStatus;
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
import com.tobe.healthy.schedule.application.CommonScheduleService;
import com.tobe.healthy.schedule.domain.dto.in.StudentScheduleCond;
import com.tobe.healthy.schedule.domain.dto.out.MyReservation;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.repository.common.CommonScheduleRepository;
import com.tobe.healthy.schedule.repository.student.StudentScheduleRepository;
import com.tobe.healthy.schedule.repository.waiting.ScheduleWaitingRepository;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.tobe.healthy.common.error.ErrorCode.*;
import static com.tobe.healthy.course.domain.dto.CourseStatus.*;
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
    private final StudentScheduleRepository studentScheduleRepository;
    private final CommonScheduleService commonScheduleService;
    private final ScheduleWaitingRepository scheduleWaitingRepository;

    public void addCourse(Long trainerId, CourseAddCommand command) {
        Member trainer = memberRepository.findByIdAndMemberTypeAndDelYnFalse(trainerId, TRAINER)
                .orElseThrow(() -> new CustomException(TRAINER_NOT_FOUND));
        Member member = memberRepository.findByIdAndMemberTypeAndDelYnFalse(command.getMemberId(), STUDENT)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        mappingRepository.findByTrainerIdAndMemberId(trainerId, member.getId())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_MAPPED));

        checkCourseAlreadyExists(member.getId());
        if(command.getLessonCnt() < 1) throw new CustomException(LESSON_CNT_NOT_VALID);
        if(500 < command.getLessonCnt()) throw new CustomException(LESSON_CNT_MAX);
        Course course = courseRepository.save(Course.create(member, trainer, command.getLessonCnt(), command.getLessonCnt()));
        courseHistoryRepository.save(CourseHistory.create(course, course.getTotalLessonCnt(), PLUS, COURSE_CREATE, trainer));
        log.info("[수강권 등록] trainer: {}, course: {}, member:{}", trainer, course, course.getMember());
    }

    private void checkCourseAlreadyExists(Long memberId) {
        Long cnt = courseRepository.countByMemberIdAndRemainLessonCntGreaterThan(memberId, 0);
        if(0 < cnt) throw new CustomException(COURSE_ALREADY_EXISTS);
    }

    public void deleteCourseByTrainer(Long trainerId, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(COURSE_NOT_FOUND));
        Long memberId = course.getMember().getId();

        //수업 진행 횟수가 1회이상이면 삭제 불가
        Long completedLessonCnt = getCompletedLessonCnt(memberId, courseId);
        if(0 < completedLessonCnt){
            throw new CustomException(COURSE_IS_USING);
        }else{
            //해당 수강권으로 예약된 수업 조회
            StudentScheduleCond searchCond = new StudentScheduleCond(null, null, null, courseId);
            List<MyReservation> result = studentScheduleRepository.findNewReservation(memberId, searchCond);

            //예약된 수업이 있으면 수강권 삭제 불가
            if(!result.isEmpty()) throw new CustomException(RESERVATION_ALREADY_EXISTS);
        }
        //대기내역 삭제
        scheduleWaitingRepository.deleteByMemberId(memberId);
        deleteCourse(trainerId, course);
    }

    public void deleteCourseAndCancelReservation(Long trainerId, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(COURSE_NOT_FOUND));
        Long memberId = course.getMember().getId();

        //해당 수강권으로 예약된 수업 조회
        StudentScheduleCond searchCond = new StudentScheduleCond(null, null, null, courseId);
        List<MyReservation> result = studentScheduleRepository.findNewReservation(memberId, searchCond);

        //예약된 수업이 있으면 수업 취소
        if(!result.isEmpty()) result.forEach(r -> commonScheduleService.cancelMemberScheduleForce(r.getScheduleId(), memberId));
        //대기내역 삭제
        scheduleWaitingRepository.deleteByMemberId(memberId);
        deleteCourse(trainerId, course);
    }

    public void deleteCourse(Long trainerId, Course course) {
        Long completedLessonCnt = getCompletedLessonCnt(course.getMember().getId(), course.getCourseId());
        course.deleteSchedule();
        Long courseId = course.getCourseId();
        Member trainer = memberRepository.findByIdAndMemberTypeAndDelYnFalse(trainerId, TRAINER)
                .orElseThrow(() -> new CustomException(TRAINER_NOT_FOUND));
        courseRepository.deleteByCourseIdAndTrainerId(courseId, trainerId);
        log.info("[수강권 삭제] trainer: {}, course: {}, member: {}, completedLessonCnt: {}", trainer, course, course.getMember(), completedLessonCnt);
    }

    public CustomPaging getCourse(Member loginMember, Pageable pageable, Long memberId, String searchDate) {
        Member member = memberRepository.findByIdAndMemberTypeAndDelYnFalse(memberId, STUDENT)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        Long trainerId = TRAINER.equals(loginMember.getMemberType()) ? loginMember.getId() : null;
        Page<CourseHistory> histories = courseHistoryRepository.getCourseHistory(memberId, trainerId, pageable, searchDate);
        List<CourseHistoryDto> courseHistoryDtos = histories.map(CourseHistoryDto::from).stream().toList();

        CustomPaging customPaging = new CustomPaging<>(courseHistoryDtos, histories.getPageable().getPageNumber(),
                histories.getPageable().getPageSize(), histories.getTotalPages(), histories.getTotalElements(), histories.isLast());

        CourseGetResult courseGetResult = CourseGetResult.create(getNowUsingCourse(memberId), member.getGym().getName());
        customPaging.setMainData(courseGetResult);
        return customPaging;
    }

    public CourseDto getNowUsingCourse(Long memberId){
        Optional<Course> optCourse = courseRepository.findTop1ByMemberIdAndRemainLessonCntGreaterThanOrderByCreatedAtDesc(memberId, -1);
        if(optCourse.isPresent()){
            CourseDto courseDto = CourseDto.from(optCourse.get());
            Long completedLessonCnt = getCompletedLessonCnt(memberId, courseDto.getCourseId());
            courseDto.setCompletedLessonCnt(completedLessonCnt.intValue());
            return courseDto;
        }
        return null;
    }

    private Long getCompletedLessonCnt(Long memberId, Long courseDto) {
        return commonScheduleRepository.getCompletedLessonCnt(memberId, courseDto);
    }

    public CourseStatus getCourseStatus(CourseDto courseDto){
        if(courseDto == null){
            return NONE;
        }else if(courseDto.getRemainLessonCnt() == 0){
            return EXPIRED;
        }else{
            return USING;
        }
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
        updateCourse(command, trainer, course, null);
    }

    public void updateCourseByMember(Long scheduleId, Long trainerId, CourseUpdateCommand command) {
        Member trainer = memberRepository.findByIdAndMemberTypeAndDelYnFalse(trainerId, TRAINER)
                .orElseThrow(() -> new CustomException(TRAINER_NOT_FOUND));
        Member member = memberRepository.findByIdAndMemberTypeAndDelYnFalse(command.getMemberId(), STUDENT)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        mappingRepository.findByTrainerIdAndMemberId(trainerId, member.getId())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_MAPPED));

        CourseDto usingCourse = getNowUsingCourse(member.getId());
        CourseStatus courseStatus = getCourseStatus(usingCourse);
        Calculation calculation = command.getCalculation();

        switch (calculation){
            case PLUS:
                if(NONE.equals(courseStatus)) throw new CustomException(LESSON_CNT_NOT_VALID);
                break;
            case MINUS:
                if(NONE.equals(courseStatus) || EXPIRED.equals(courseStatus)) throw new CustomException(LESSON_CNT_NOT_VALID);
                break;
        }

        Course course = courseRepository.findById(usingCourse.getCourseId())
                    .orElseThrow(() -> new CustomException(LESSON_CNT_NOT_VALID));

        updateCourse(command, trainer, course, scheduleId);
        updateScheduleCourse(scheduleId, course, calculation);
    }

    private void updateCourse(CourseUpdateCommand command, Member trainer, Course course, Long scheduleId) {
        switch (command.getCalculation()){
            case PLUS:
                if(command.getUpdateCnt() < 1) throw new CustomException(COURSE_ONLY_PLUS);
                break;
            case MINUS:
                if(command.getUpdateCnt() < 1) throw new CustomException(COURSE_POSITIVE);
                break;
        }

        int result = command.getCalculation().apply(course.getRemainLessonCnt(), command.getUpdateCnt());
        if (result < 0) throw new CustomException(LESSON_CNT_NOT_VALID);
        if (500 < result) throw new CustomException(LESSON_CNT_MAX);

        course.updateRemainLessonCnt(command);
        //수강권 변경 주체가 트레이너인 경우 -> 총 횟수도 함께 업데이트
        if (CourseHistoryType.getEnumByGroup(TRAINER).contains(command.getType())) {
            course.updateTotalLessonCnt(command);
        }
        CourseHistory history = courseHistoryRepository.save(CourseHistory.create(course, command.getUpdateCnt(), command.getCalculation(), command.getType(), trainer));
        log.info("[수강권 증감] trainer: {}, course: {}, history: {}, member: {}, scheduleId: {}", trainer, course, history, course.getMember(), scheduleId);
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