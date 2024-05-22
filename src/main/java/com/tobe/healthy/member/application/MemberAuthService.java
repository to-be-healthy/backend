package com.tobe.healthy.member.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tobe.healthy.common.redis.RedisKeyPrefix;
import com.tobe.healthy.common.redis.RedisService;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.member.domain.dto.in.RetrieveMemberId;
import com.tobe.healthy.member.domain.dto.in.RetrieveMemberId.FindMemberIdResult;
import com.tobe.healthy.member.domain.dto.out.InvitationMappingResult;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.tobe.healthy.common.Utils.validateUserId;
import static com.tobe.healthy.config.error.ErrorCode.*;
import static io.micrometer.common.util.StringUtils.isEmpty;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberAuthService {

    private final MemberRepository memberRepository;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    public boolean validateUserIdDuplication(String userId) {
        if (validateUserId(userId)) {
            throw new CustomException(MEMBER_ID_NOT_VALID);
        }
        memberRepository.findByUserId(userId).ifPresent(m -> {
            throw new CustomException(MEMBER_ID_DUPLICATION);
        });
        return true;
    }

    public Boolean validateEmailDuplication(String email) {
        memberRepository.findByEmail(email).ifPresent(m -> {
            throw new CustomException(MEMBER_EMAIL_DUPLICATION);
        });
        return true;
    }

    public FindMemberIdResult findUserId(RetrieveMemberId request) {
        Member member = memberRepository.findByEmailAndName(request.getEmail(), request.getName())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        return new FindMemberIdResult(
            member.getUserId().substring(0, member.getUserId().length() - 2) + "**",
                  member.getCreatedAt()
        );
    }

    public InvitationMappingResult getInvitationMapping(String uuid) {
        Map<String, String> map = getInviteMappingData(uuid);
        Long trainerId = Long.valueOf(map.get("trainerId"));
        String name = map.get("name");
        int lessonCnt = Integer.parseInt(map.get("lessonCnt"));
        Member member = memberRepository.findByMemberIdWithGym(trainerId);
        return InvitationMappingResult.create(member, name, lessonCnt);
    }

    private Map<String, String> getInviteMappingData(String uuid) {
        String invitationKey = RedisKeyPrefix.INVITATION.getDescription() + uuid;
        String mappedData = redisService.getValues(invitationKey);
        if (isEmpty(mappedData)) {
            throw new CustomException(INVITE_LINK_NOT_FOUND);
        }
        HashMap<String, String> map = new HashMap<>();
        try {
            map = objectMapper.readValue(mappedData, HashMap.class);
        } catch (JsonProcessingException e) {
            log.error("error => {}", e.getStackTrace()[0]);
        }
        return map;
    }
}