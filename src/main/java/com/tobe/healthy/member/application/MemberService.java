package com.tobe.healthy.member.application;

import static com.tobe.healthy.config.error.ErrorCode.MEMBER_DUPLICATION_EMAIL;
import static com.tobe.healthy.config.error.ErrorCode.MEMBER_DUPLICATION_NICKNAME;
import static com.tobe.healthy.config.error.ErrorCode.MEMBER_NOT_FOUND;
import static com.tobe.healthy.config.error.ErrorCode.REFRESH_TOKEN_EXPIRED;
import static com.tobe.healthy.config.error.ErrorCode.REFRESH_TOKEN_NOT_FOUND;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.config.security.JwtTokenGenerator;
import com.tobe.healthy.config.security.JwtTokenProvider;
import com.tobe.healthy.member.domain.dto.in.MemberLoginCommand;
import com.tobe.healthy.member.domain.dto.in.MemberRegisterCommand;
import com.tobe.healthy.member.domain.dto.out.MemberRegisterCommandResult;
import com.tobe.healthy.member.domain.entity.BearerToken;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.Tokens;
import com.tobe.healthy.member.repository.BearerTokenRepository;
import com.tobe.healthy.member.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final JwtTokenGenerator tokenGenerator;
    private final JwtTokenProvider tokenProvider;
    private final BearerTokenRepository bearerTokenRepository;

    @Transactional
    public MemberRegisterCommandResult create(MemberRegisterCommand request) {
        validateDuplicateEmail(request);
        validateDuplicateNickname(request);

        String password = passwordEncoder.encode(request.getPassword());
        Member member = Member.create(request, password);
        memberRepository.save(member);

        return MemberRegisterCommandResult.of(member);
    }

    @Transactional
    public Tokens login(MemberLoginCommand request) {
        return memberRepository.findByEmail(request.getEmail())
            .filter(member -> passwordEncoder.matches(request.getPassword(), member.getPassword()))
            .map(tokenGenerator::create)
            .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }

    @Transactional
    public Tokens refresh(String refreshToken) {
        try {
            tokenProvider.decode(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new CustomException(REFRESH_TOKEN_EXPIRED);
        }

        BearerToken token = bearerTokenRepository.findByRefreshToken(refreshToken).orElseThrow(
            () -> new CustomException(REFRESH_TOKEN_NOT_FOUND));

        Long memberId = token.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(
            ()-> new CustomException(MEMBER_NOT_FOUND));

        return tokenGenerator.create(member);
    }

    public Boolean isAvailableEmail(String email) {
        return memberRepository.findByEmail(email).isEmpty();
    }

    private void validateDuplicateEmail(MemberRegisterCommand request) {
        memberRepository.findByEmail(request.getEmail()).ifPresent(m -> {
            throw new CustomException(MEMBER_DUPLICATION_EMAIL);
        });
    }

    private void validateDuplicateNickname(MemberRegisterCommand request) {
        memberRepository.findByNickname(request.getNickname()).ifPresent(m -> {
            throw new CustomException(MEMBER_DUPLICATION_NICKNAME);
        });
    }
}
