package com.tobe.healthy.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tobe.healthy.config.error.exception.MemberDuplicateException;
import com.tobe.healthy.config.error.exception.MemberNotFoundException;
import com.tobe.healthy.config.security.JwtTokenGenerator;
import com.tobe.healthy.config.security.JwtTokenProvider;
import com.tobe.healthy.member.domain.dto.in.MemberLoginCommand;
import com.tobe.healthy.member.domain.dto.in.MemberRegisterCommand;
import com.tobe.healthy.member.domain.dto.out.MemberRegisterCommandResult;
import com.tobe.healthy.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired PasswordEncoder passwordEncoder;
    @Autowired MemberRepository memberRepository;
    @Autowired MemberService memberService;
    @Autowired JwtTokenGenerator tokenGenerator;
    @Autowired JwtTokenProvider jwtTokenProvider;

    @Test
    void save() {
        String path = "/api/auth/join";
        log.info("path = {}", path.startsWith("/api/auth/"));
    }

    @Test
    public void joinMember() {
        MemberRegisterCommand command = new MemberRegisterCommand("laborlawseon@gmail.com", "12345678", "정선우");
        String password = passwordEncoder.encode(command.getPassword());
//        Member member = new Member(command.getEmail(), password);
//        memberRepository.save(member);
//        member = memberRepository.findById(member.getId()).orElseThrow();
//        assertThat(member.getId()).isEqualTo(member.getId());
    }

    @Test
    void duplicateMember() {
        // given
        MemberRegisterCommand command = new MemberRegisterCommand("laborlawseon@gmail.com", "12345678", "정선우");
        memberService.create(command);

        // when

        assertThatThrownBy(() -> memberService.create(command))
            .isInstanceOf(MemberDuplicateException.class);

        // then

    }

    @Test
    void loginMember() {
        // given
        MemberRegisterCommand command = new MemberRegisterCommand("laborlawseon@gmail.com", "12345678", "정선우");
        memberService.create(command);
        MemberLoginCommand loginCommand = new MemberLoginCommand("laborlawseon22@gmail.com", "12345678");

        // when
        assertThatThrownBy(() -> memberService.login(loginCommand))
            .isInstanceOf(MemberNotFoundException.class);
    }


    @Test
    void refreshTokenForMember() {
        // given
        MemberRegisterCommand command = new MemberRegisterCommand("laborlawseon@gmail.com", "12345678", "정선우");
        MemberRegisterCommandResult memberRegisterCommandResult = memberService.create(command);

        // when
//        Tokens refresh = memberService.refresh(memberRegisterCommandResult.getRefreshToken());
//        log.info(refresh.getAccessToken());
    }

    @Test
    void validateEmailIsEmpty() {
        // given
        assertThat(memberService.isAvailableEmail("laborlawseon@gmail.com")).isTrue();
    }
}