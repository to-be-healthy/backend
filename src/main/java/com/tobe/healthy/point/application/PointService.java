package com.tobe.healthy.point.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.point.domain.dto.PointDto;
import com.tobe.healthy.point.domain.dto.RankDto;
import com.tobe.healthy.point.domain.dto.out.PointGetResult;
import com.tobe.healthy.point.domain.entity.Calculation;
import com.tobe.healthy.point.domain.entity.Point;
import com.tobe.healthy.point.domain.entity.PointType;
import com.tobe.healthy.point.repository.PointRepository;
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
import static com.tobe.healthy.point.domain.entity.Calculation.MINUS;
import static com.tobe.healthy.point.domain.entity.Calculation.PLUS;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PointService {

    private final TrainerMemberMappingRepository mappingRepository;
    private final PointRepository pointRepository;
    private final MemberRepository memberRepository;

    public void updateMemberRank() {
        List<Long> trainerIds = mappingRepository.findAllTrainerIds();
        List<TrainerMemberMapping> members;
        for(Long trainerId : trainerIds){
            members = mappingRepository.findAllByTrainerId(trainerId);
            List<Long> memberIds = members.stream().map(m -> m.getMember().getId()).toList();
            List<RankDto> ranks = pointRepository.calculateRank(memberIds).stream().toList()
                    .stream().map(obj -> new RankDto(((Long) obj[0]).intValue(), (Long) obj[1], ((BigDecimal) obj[2]).intValue()))
                    .collect(Collectors.toList());

            for(TrainerMemberMapping thisMember : members){
                List<RankDto> thisRankDto = ranks.stream().filter(r -> r.getMemberId().equals(thisMember.getMember().getId())).toList();
                thisMember.changeRanking(thisRankDto.isEmpty() ? 999 : thisRankDto.get(0).getRanking());
            }
        }
    }

    public void updatePoint(Long memberId, PointType type, Calculation calculation, int point){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        if(PLUS == calculation){
            LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0));
            LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59));
            long cnt = pointRepository.countByMemberIdAndTypeAndCalculationAndCreatedAtBetween(memberId, type, calculation, start, end);
            if(0 < cnt) return;
        }
        pointRepository.save(Point.create(member, type, calculation, point));
    }

    public PointGetResult getPoint(Member member, String searchDate, Pageable pageable) {
        Long memberId = member.getId();
        memberRepository.findByIdAndMemberTypeAndDelYnFalse(memberId, STUDENT)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        Page<Point> histories = pointRepository.getPoint(memberId, searchDate, pageable);
        int point = pointRepository.getSumPoint(memberId, searchDate);
        List<PointDto> pointHistoryDtos = histories.map(PointDto::from).stream().toList();
        return PointGetResult.create(point, pointHistoryDtos.isEmpty() ? null : pointHistoryDtos);
    }
}
