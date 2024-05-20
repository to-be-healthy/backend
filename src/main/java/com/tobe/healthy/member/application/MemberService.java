package com.tobe.healthy.member.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.member.domain.dto.out.MemberInfoResult;
import com.tobe.healthy.member.domain.dto.out.TrainerMappingResult;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.tobe.healthy.config.error.ErrorCode.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final TrainerMemberMappingRepository mappingRepository;

    public MemberInfoResult getMemberInfo(Long memberId) {
        Member member = memberRepository.findByIdAndDelYnFalse(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        return MemberInfoResult.create(member);
    }

    public TrainerMappingResult getTrainerMapping(Member member) {
        TrainerMemberMapping mapping = mappingRepository.findTop1ByMemberIdOrderByCreatedAtDesc(member.getId()).orElse(null);
        return new TrainerMappingResult(mapping != null);
    }
}
