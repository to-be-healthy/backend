package com.tobe.healthy.home.application;

import com.tobe.healthy.common.error.CustomException;
import com.tobe.healthy.course.application.CourseService;
import com.tobe.healthy.course.domain.dto.CourseDto;
import com.tobe.healthy.course.repository.CourseHistoryRepository;
import com.tobe.healthy.diet.application.DietService;
import com.tobe.healthy.diet.domain.dto.DietDto;
import com.tobe.healthy.gym.domain.dto.out.GymDto;
import com.tobe.healthy.lessonhistory.domain.dto.out.RetrieveLessonHistoryByDateCondResult;
import com.tobe.healthy.lessonhistory.repository.LessonHistoryRepository;
import com.tobe.healthy.member.domain.dto.out.MemberInTeamResult;
import com.tobe.healthy.member.domain.dto.out.StudentHomeResult;
import com.tobe.healthy.member.domain.dto.out.TrainerHomeResult;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.notification.application.NotificationService;
import com.tobe.healthy.point.domain.dto.out.PointDto;
import com.tobe.healthy.point.domain.dto.out.RankDto;
import com.tobe.healthy.point.repository.PointRepository;
import com.tobe.healthy.schedule.domain.dto.out.MyReservation;
import com.tobe.healthy.schedule.domain.dto.out.RetrieveTrainerScheduleByLessonDtResult;
import com.tobe.healthy.schedule.repository.TrainerScheduleRepository;
import com.tobe.healthy.schedule.repository.student.StudentScheduleRepository;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.tobe.healthy.common.error.ErrorCode.MEMBER_NOT_FOUND;
import static com.tobe.healthy.member.domain.entity.MemberType.STUDENT;
import static com.tobe.healthy.member.domain.entity.MemberType.TRAINER;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HomeService {
    private final PointRepository pointRepository;
    private final StudentScheduleRepository studentScheduleRepository;
    private final LessonHistoryRepository lessonHistoryRepository;
    private final DietService dietService;
    private final TrainerMemberMappingRepository mappingRepository;
    private final TrainerScheduleRepository trainerScheduleRepository;
    private final MemberRepository memberRepository;
    private final CourseService courseService;
    private final CourseHistoryRepository courseHistoryRepository;
    private final NotificationService notificationService;

    public StudentHomeResult getStudentHome(Long memberId) {
        //헬스장 정보
        Member member = memberRepository.findByIdAndMemberTypeAndDelYnFalse(memberId, STUDENT)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        GymDto gym = member.getGym() == null ? null : GymDto.from(member.getGym());


        //트레이너 매핑 여부
        TrainerMemberMapping mapping = mappingRepository.findTop1ByMemberIdOrderByCreatedAtDesc(memberId).orElse(null);
        boolean isMapped = mapping != null;

        //수강권
        CourseDto usingCourse = courseService.getNowUsingCourse(memberId);

        //포인트
        String yyyyMM = getNowMonth();
        int monthPoint = pointRepository.getPointOfSearchMonth(memberId, yyyyMM);
        int totalPoint = pointRepository.getTotalPoint(memberId, yyyyMM);
        PointDto point = PointDto.create(yyyyMM, monthPoint, totalPoint);

        //랭킹
        RankDto rank = new RankDto();
        if(isMapped){
            long totalMemberCnt = mappingRepository.countByTrainerId(mapping.getTrainer().getId());
            rank.setRanking(mapping.getRanking());
            rank.setLastMonthRanking(mapping.getLastMonthRanking());
            rank.setTotalMemberCnt((int) totalMemberCnt);
        }

        //다음 PT 예정일
        MyReservation myReservation = studentScheduleRepository.findMyNextReservation(memberId);

        //수업일지
        RetrieveLessonHistoryByDateCondResult lessonHistory = lessonHistoryRepository.findTop1LessonHistoryByMemberId(memberId);

        //식단
        DietDto diet = dietService.getTodayDiet(memberId);

        // 알림 레드닷 여부
        boolean redDotStatus = notificationService.findRedDotStatus(memberId);

        return StudentHomeResult.create(usingCourse, point, rank, myReservation, lessonHistory, diet, gym, redDotStatus);
    }

    public TrainerHomeResult getTrainerHome(Long trainerId) {

        // 사용자 정보
        Member member = memberRepository.findByIdAndMemberTypeAndDelYnFalse(trainerId, TRAINER)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        //헬스장 정보
        GymDto gym = member.getGym() == null ? null : GymDto.from(member.getGym());

        long mappingStudentCount = mappingRepository.countByTrainerId(trainerId);

        // 우수회원
        List<MemberInTeamResult> bestStudents = null;
        Long paidCnt = courseHistoryRepository.checkPaidOneLesson(trainerId, getNowMonth());
        if(paidCnt.intValue() == 0){
            bestStudents = memberRepository.getBestStudent(trainerId);

            //수강권
            for (MemberInTeamResult bestStudent : bestStudents) {
                CourseDto usingCourse = courseService.getNowUsingCourse(bestStudent.getMemberId());
                bestStudent.setCourseId(usingCourse == null ? null : usingCourse.getCourseId());
            }
        }

        RetrieveTrainerScheduleByLessonDtResult trainerTodaySchedule = trainerScheduleRepository.findOneTrainerTodaySchedule(null, trainerId);

        // 알림 레드닷 여부
        boolean redDotStatus = notificationService.findRedDotStatus(trainerId);

        return TrainerHomeResult.builder()
                .studentCount(mappingStudentCount)
                .bestStudents(bestStudents)
                .todaySchedule(trainerTodaySchedule)
                .gym(gym)
                .redDotStatus(redDotStatus)
                .build();
    }

    private String getNowMonth() {
        return LocalDate.now().toString().substring(0, 7);
    }
}
