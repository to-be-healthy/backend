package com.tobe.healthy.gym.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.gym.domain.dto.in.MembershipAddCommand;
import com.tobe.healthy.gym.domain.entity.Gym;
import com.tobe.healthy.gym.domain.entity.GymMembership;
import com.tobe.healthy.gym.repository.GymMembershipRepository;
import com.tobe.healthy.gym.repository.GymRepository;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.tobe.healthy.config.error.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class GymMembershipService {

    private final GymMembershipRepository gymMembershipRepository;
    private final MemberRepository memberRepository;
    private final GymRepository gymRepository;

    public void registerGymMembership(MembershipAddCommand command) {
        Member member = memberRepository.findById(command.getMemberId())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        Gym gym = gymRepository.findById(command.getGymId())
                .orElseThrow(() -> new CustomException(GYM_NOT_FOUND));

        if(command.getGymStartDt().isAfter(command.getGymEndDt())){
            throw new CustomException(DATETIME_NOT_VALID);
        }
        member.registerGym(gym);
        GymMembership gymMembership = new GymMembership(gym, member, command.getLessonCnt(),
                command.getLessonCnt(), command.getGymStartDt(), command.getGymEndDt());
        gymMembershipRepository.save(gymMembership);
    }

}
