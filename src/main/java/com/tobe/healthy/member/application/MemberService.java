package com.tobe.healthy.member.application;

import static com.tobe.healthy.member.domain.dto.out.MemberRegisterCommandResult.from;

import com.tobe.healthy.config.error.exception.CustomIllegalArgumentException;
import com.tobe.healthy.config.error.exception.MemberDuplicateException;
import com.tobe.healthy.config.error.exception.MemberNotFoundException;
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
import java.util.Optional;
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
        validateMember(request);
        String password = passwordEncoder.encode(request.getPassword());
        Member member = Member.create(request.getEmail(), password, request.getNickname());
        memberRepository.save(member);
        member = memberRepository.findById(member.getId())
            .orElseThrow(() -> new CustomIllegalArgumentException("회원가입중 오류가 발생하였습니다."));
        return from(member);
    }

    private void validateMember(MemberRegisterCommand request) {
        Optional<Member> member = memberRepository.findByEmail(request.getEmail());
        if (member.isPresent()) {
            throw new MemberDuplicateException("중복된 이메일이 존재합니다.");
        }
    }

    @Transactional
    public Tokens login(MemberLoginCommand request) {
        return memberRepository.findByEmail(request.getEmail())
            .filter(member -> passwordEncoder.matches(request.getPassword(), member.getPassword()))
            .map(tokenGenerator::create)
            .orElseThrow(() -> new MemberNotFoundException("이메일 또는 비밀번호를 잘못 입력하셨습니다."));
    }

    @Transactional
    public Tokens refresh(String refreshToken) {
        try {
            tokenProvider.decode(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "Refresh token was expired.", e);
        }

        BearerToken token = bearerTokenRepository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> new IllegalArgumentException("Could not find refresh token."));

        Long memberId = token.getMemberId();
        Member member = memberRepository.findById(memberId)
            .orElseThrow(()-> new IllegalArgumentException("Not Found User"));

        return tokenGenerator.create(member);
    }

    public Boolean isAvailableEmail(String email) {
        return memberRepository.findByEmail(email).isEmpty();
    }
}
