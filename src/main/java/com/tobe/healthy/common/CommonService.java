package com.tobe.healthy.common;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.config.error.ErrorCode;
import com.tobe.healthy.config.security.JwtTokenProvider;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    public Member getMemberByToken(String bearerToken){
        String accessToken = bearerToken.substring(7);
        log.info("accessToken: {}", accessToken);
        Long id = jwtTokenProvider.getIdFromToken(accessToken);
        Optional<Member> member = memberRepository.findById(id);
        member.orElseThrow(()-> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return member.get();
    }
}
