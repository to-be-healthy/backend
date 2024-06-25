package com.tobe.healthy.member.application;

import com.tobe.healthy.common.error.CustomException;
import com.tobe.healthy.member.domain.dto.in.ValidateCurrentPassword;
import com.tobe.healthy.member.domain.dto.out.MemberInfoResult;
import com.tobe.healthy.member.domain.dto.out.RetrieveTrainerInfo;
import com.tobe.healthy.member.domain.dto.out.TrainerMappingResult;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.tobe.healthy.common.error.ErrorCode.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final TrainerMemberMappingRepository mappingRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberInfoResult getMemberInfo(Long memberId) {
        Member member = memberRepository.findByIdAndDelYnFalse(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        return MemberInfoResult.create(member);
    }

    public TrainerMappingResult getTrainerMapping(Member member) {
        TrainerMemberMapping mapping = mappingRepository.findTop1ByMemberIdOrderByCreatedAtDesc(member.getId()).orElse(null);
        return new TrainerMappingResult(mapping != null);
    }

    public Boolean validateCurrentPassword(ValidateCurrentPassword request, Long memberId) {
        memberRepository.findById(memberId).ifPresentOrElse(
            m -> {
                if (!passwordEncoder.matches(request.getPassword(), m.getPassword())) {
                    throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
                }
            }, () -> {
                throw new CustomException(MEMBER_NOT_FOUND);
            });
        return true;
    }

    public RetrieveTrainerInfo findMyTrainerInfo(Long studentId) {
        return mappingRepository.findTrainerInfoByMemberId(studentId)
                .map(RetrieveTrainerInfo::from)
                .orElse(null);
    }
}
