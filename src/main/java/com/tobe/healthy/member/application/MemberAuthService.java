package com.tobe.healthy.member.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tobe.healthy.common.error.CustomException;
import com.tobe.healthy.common.redis.RedisService;
import com.tobe.healthy.member.domain.dto.in.CommandValidateEmail;
import com.tobe.healthy.member.domain.dto.in.FindMemberUserId;
import com.tobe.healthy.member.domain.dto.in.FindMemberUserId.FindMemberUserIdResult;
import com.tobe.healthy.member.domain.dto.out.InvitationMappingResult;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.NonMember;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.member.repository.NonMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.tobe.healthy.common.Utils.validateUserId;
import static com.tobe.healthy.common.error.ErrorCode.*;
import static com.tobe.healthy.member.domain.entity.SocialType.NONE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberAuthService {

    private final MemberRepository memberRepository;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private final NonMemberRepository nonMemberRepository;

    public boolean validateUserIdDuplication(String userId) {
        if (validateUserId(userId)) {
            throw new CustomException(MEMBER_ID_NOT_VALID);
        }

        memberRepository.findByUserId(userId).ifPresent(m -> {
            throw new CustomException(MEMBER_ID_DUPLICATION);
        });

        return true;
    }

    public Boolean validateEmailDuplication(CommandValidateEmail request) {
        memberRepository.findByEmail(request.getEmail()).ifPresent(m -> {
            throw new CustomException(MEMBER_EMAIL_DUPLICATION);
        });
        return true;
    }

    public FindMemberUserIdResult findUserId(FindMemberUserId request) {

        Member member = memberRepository.findByEmailAndName(request.getEmail(), request.getName())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (member.getSocialType() != NONE) {
            return FindMemberUserIdResult.from(
                    member,
                    String.format("%s은 %s 계정으로 가입되어 있습니다.", member.getEmail(), member.getSocialType().getDescription())
            );
        }

        return FindMemberUserIdResult.from(member, "아이디 찾기에 성공하였습니다.");
    }

    public InvitationMappingResult getInvitationMapping(String uuid) {
        NonMember nonMember = getNonmemberData(uuid);
        Member member = memberRepository.findByIdAndDelYnFalse(nonMember.getTrainerId())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        return InvitationMappingResult.create(member, nonMember.getName(), nonMember.getLessonCnt());
    }

    private NonMember getNonmemberData(String uuid) {
        String invitationLink = "https://main.to-be-healthy.site/invite?type=student&uuid=" + uuid;
        NonMember nonMember = nonMemberRepository.findByInvitationLink(invitationLink)
                .orElseThrow(() -> new CustomException(INVITE_LINK_NOT_FOUND));
        return nonMember;
    }
}
