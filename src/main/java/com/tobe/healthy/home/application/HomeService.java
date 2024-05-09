package com.tobe.healthy.home.application;

import com.tobe.healthy.course.domain.dto.CourseDto;
import com.tobe.healthy.course.domain.entity.Course;
import com.tobe.healthy.course.repository.CourseRepository;
import com.tobe.healthy.diet.application.DietService;
import com.tobe.healthy.diet.domain.dto.DietDto;
import com.tobe.healthy.lesson_history.domain.dto.out.LessonHistoryResponse;
import com.tobe.healthy.lesson_history.repository.LessonHistoryRepository;
import com.tobe.healthy.member.domain.dto.out.MemberInTeamResult;
import com.tobe.healthy.member.domain.dto.out.StudentHomeResult;
import com.tobe.healthy.member.domain.dto.out.TrainerHomeResult;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.point.domain.dto.out.PointDto;
import com.tobe.healthy.point.domain.dto.out.RankDto;
import com.tobe.healthy.point.repository.PointRepository;
import com.tobe.healthy.schedule.domain.dto.out.MyReservation;
import com.tobe.healthy.schedule.entity.in.TrainerTodayScheduleSearchCond;
import com.tobe.healthy.schedule.entity.out.TrainerTodayScheduleResponse;
import com.tobe.healthy.schedule.repository.student.StudentScheduleRepository;
import com.tobe.healthy.schedule.repository.trainer.TrainerScheduleRepository;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HomeService {
    private final CourseRepository courseRepository;
    private final PointRepository pointRepository;
    private final StudentScheduleRepository studentScheduleRepository;
    private final LessonHistoryRepository lessonHistoryRepository;
    private final DietService dietService;
    private final TrainerMemberMappingRepository mappingRepository;
    private final TrainerScheduleRepository trainerScheduleRepository;
    private final MemberRepository memberRepository;

    public StudentHomeResult getStudentHome(Long memberId) {
        //트레이너 매핑 여부
        TrainerMemberMapping mapping = mappingRepository.findTop1ByMemberIdOrderByCreatedAtDesc(memberId).orElse(null);
        boolean isMapped = mapping != null;

        //수강권
        Optional<Course> optCourse = courseRepository.findTop1ByMemberIdAndRemainLessonCntGreaterThanOrderByCreatedAtDesc(memberId, -1);
        CourseDto usingCourse = optCourse.map(CourseDto::from).orElse(null);

        //포인트
        int monthPoint = pointRepository.getPointOfSearchMonth(memberId, getNowMonth());
        int totalPoint = pointRepository.getTotalPoint(memberId);
        PointDto point = PointDto.create(monthPoint, totalPoint );

        //랭킹
        RankDto rank = new RankDto();
        if(isMapped){
            long totalMemberCnt = mappingRepository.countByTrainerId(mapping.getTrainer().getId());
            rank.setRanking(mapping.getRanking());
            rank.setTotalMemberCnt((int) totalMemberCnt);
        }

        //다음 PT 예정일
        MyReservation myReservation = studentScheduleRepository.findMyNextReservation(memberId);

        //수업일지
        LessonHistoryResponse lessonHistory = lessonHistoryRepository.findTop1LessonHistoryByMemberId(memberId);

        //식단
        DietDto diet = dietService.getDietCreatedAtToday(memberId);

        return StudentHomeResult.create(isMapped, usingCourse, point, rank, myReservation, lessonHistory, diet);
    }

    public TrainerHomeResult getTrainerHome(TrainerTodayScheduleSearchCond request, Long trainerId) {
        long mappingStudentCount = mappingRepository.countByTrainerId(trainerId);

        // 우수회원 추가 필요
        List<MemberInTeamResult> bestStudents = memberRepository.getBestStudent(trainerId);

        TrainerTodayScheduleResponse trainerTodaySchedule = trainerScheduleRepository.findOneTrainerTodaySchedule(request, trainerId);

        return TrainerHomeResult.builder()
                .studentCount(mappingStudentCount)
                .bestStudents(bestStudents)
                .todaySchedule(trainerTodaySchedule)
                .build();
    }

    private String getNowMonth() {
        return LocalDate.now().toString().substring(0, 7);
    }
}
