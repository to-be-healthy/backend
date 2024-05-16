package com.tobe.healthy.point.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.point.domain.dto.PointHistoryDto;
import com.tobe.healthy.point.domain.dto.TempRankDto;
import com.tobe.healthy.point.domain.dto.out.PointDto;
import com.tobe.healthy.point.domain.entity.Calculation;
import com.tobe.healthy.point.domain.entity.Point;
import com.tobe.healthy.point.domain.entity.PointType;
import com.tobe.healthy.point.repository.PointRepository;
import com.tobe.healthy.schedule.domain.dto.out.MyReservation;
import com.tobe.healthy.schedule.repository.student.StudentScheduleRepository;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.tobe.healthy.config.error.ErrorCode.MEMBER_NOT_FOUND;
import static com.tobe.healthy.member.domain.entity.MemberType.STUDENT;
import static com.tobe.healthy.point.domain.entity.Calculation.PLUS;
import static com.tobe.healthy.point.domain.entity.PointType.DIET;
import static com.tobe.healthy.point.domain.entity.PointType.WORKOUT;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PointService {

    private final TrainerMemberMappingRepository mappingRepository;
    private final PointRepository pointRepository;
    private final MemberRepository memberRepository;
    private final StudentScheduleRepository studentScheduleRepository;

    public void updateMemberRank() {
        List<Long> trainerIds = mappingRepository.findAllTrainerIds();
        List<TrainerMemberMapping> members;
        for(Long trainerId : trainerIds){
            members = mappingRepository.findAllByTrainerId(trainerId);
            List<Long> memberIds = members.stream().map(m -> m.getMember().getId()).toList();
            List<TempRankDto> ranks = pointRepository.calculateRank(memberIds).stream().toList()
                    .stream().map(obj -> new TempRankDto(((Long) obj[0]).intValue(), (Long) obj[1], ((BigDecimal) obj[2]).intValue()))
                    .collect(Collectors.toList());

            for(TrainerMemberMapping thisMember : members){
                List<TempRankDto> thisRankDto = ranks.stream().filter(r -> r.getMemberId().equals(thisMember.getMember().getId())).toList();
                thisMember.changeRanking(thisRankDto.isEmpty() ? 999 : thisRankDto.get(0).getRanking());
            }
        }
    }

    public void updatePoint(Long memberId, PointType type, Calculation calculation, int point){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        //마지막 PT일자 지난 경우 포인트 미지급
        if(DIET == type || WORKOUT == type){
            MyReservation myNextReservation = studentScheduleRepository.findMyNextReservation(memberId);
            if(myNextReservation == null) return;
        }

        if(PLUS == calculation){
            LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0));
            LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59));
            long cnt = pointRepository.countByMemberIdAndTypeAndCalculationAndCreatedAtBetween(memberId, type, calculation, start, end);
            if(0 < cnt) return;
        }
        pointRepository.save(Point.create(member, type, calculation, point));
    }

    public PointDto getPoint(Member member, String searchDate, Pageable pageable) {
        Long memberId = member.getId();
        memberRepository.findByIdAndMemberTypeAndDelYnFalse(memberId, STUDENT)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        Page<Point> histories = pointRepository.getPoint(memberId, searchDate, pageable);
        int monthPoint = pointRepository.getPointOfSearchMonth(memberId, searchDate);
        int totalPoint = pointRepository.getTotalPoint(memberId, searchDate);
        return PointDto.create(monthPoint, totalPoint, histories.map(PointHistoryDto::from).stream().toList());
    }
}
