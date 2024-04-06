package com.tobe.healthy.point.application;

import com.tobe.healthy.point.domain.dto.RankDto;
import com.tobe.healthy.point.repository.PointRepository;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PointService {

    private final TrainerMemberMappingRepository mappingRepository;
    private final PointRepository pointRepository;

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

}
