package com.tobe.healthy.member.application;

import static com.tobe.healthy.member.domain.entity.AlarmStatus.ENABLED;
import static com.tobe.healthy.member.domain.entity.MemberType.MEMBER;
import static org.assertj.core.api.Assertions.assertThat;

import com.tobe.healthy.common.RedisService;
import com.tobe.healthy.file.application.FileService;
import com.tobe.healthy.member.domain.dto.in.MemberFindIdCommandRequest;
import com.tobe.healthy.member.domain.dto.in.MemberFindPWCommand;
import com.tobe.healthy.member.domain.dto.in.MemberLoginCommand;
import com.tobe.healthy.member.domain.dto.in.VerifyAuthMailRequest;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.Tokens;
import jakarta.persistence.EntityManager;
import java.io.FileInputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
@DisplayName("회원 기능 테스트")
class MemberServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private RedisService redisService;

    @Autowired
    private FileService fileService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder encoder;

    @Nested
    @DisplayName("회원가입")
    class MemberJoin {

        @Test
        @DisplayName("이메일 인증번호를 전송하고 검증한다.")
        void verifyAuth() {
            // given
            String email = "laborlawseon@gmail.com";
            String sendEmail = memberService.sendAuthMail(email);

            String authKey = redisService.getValues(sendEmail);
            VerifyAuthMailRequest request = VerifyAuthMailRequest.builder().email(sendEmail)
                .authKey(authKey).build();

            assertThat(memberService.verifyAuthMail(request)).isEqualTo(sendEmail);
        }

        @Test
        @DisplayName("회원가입을 한다.")
        void registerMember() {
            Member member = Member.builder()
                .email("laborlawseon@gmail.com")
                .password("12345678")
                .nickname("seonwoo_jung")
                .alarmStatus(ENABLED)
                .memberType(MEMBER)
                .build();

            em.persist(member);

            Member findMember = em.find(Member.class, member.getId());
            assertThat(member.getId()).isEqualTo(findMember.getId());
        }

        @Test
        @DisplayName("프로필 사진을 등록한다.")
        void registerProfile() throws IOException {
            // given
            Member member = Member.builder()
                .email("laborlawseon@gmail.com")
                .password("12345678")
                .nickname("seonwoo_jung")
                .alarmStatus(ENABLED)
                .memberType(MEMBER)
                .build();

            em.persist(member);

            MockMultipartFile image = new MockMultipartFile(
                "img_640x640.jpg",
                "img_640x640" + "." + "jpg",
                "jpg",
                new FileInputStream("upload/img_640x640.jpg"));

            fileService.uploadFile(image, member.getId());
        }
    }

    @Nested
    @DisplayName("로그인 및 토큰을 갱신한다.")
    class MemberLogin {

        private Member member;

        @BeforeEach
        @DisplayName("회원을 등록한다.")
		void setup() {
            member = Member.builder()
                .email("laborlawseon@gmail.com")
                .password(encoder.encode("12345678"))
                .nickname("seonwoo_jung")
                .alarmStatus(ENABLED)
                .memberType(MEMBER)
                .build();
            em.persist(member);
        }

        @Test
        @DisplayName("로그인을 진행한다.")
        void login() {
            Tokens tokens = memberService.login(new MemberLoginCommand(member.getEmail(), "12345678"));
            log.info("tokens => {}", tokens);
        }

        @Test
        @DisplayName("토큰을 갱신한다.")
        void refreshToken() {
            Tokens before = memberService.login(new MemberLoginCommand(member.getEmail(), "12345678"));

            Tokens after = memberService.refreshToken(member.getEmail(), before.getRefreshToken());

            log.info("before.AccessToken => {}", before.getAccessToken());
            log.info("before.RefreshToken => {}", before.getRefreshToken());
            log.info("after.AccessToken => {}", after.getAccessToken());
            log.info("after.RefreshToken => {}", after.getRefreshToken());
            assertThat(before.getAccessToken()).isNotEqualTo(after.getAccessToken());
        }
    }

    @Nested
    @DisplayName("아이디, 비밀번호 찾기")
    class MemberFind {

        private Member member;

        @BeforeEach
        @DisplayName("회원을 등록한다.")
        void setup() {
            member = Member.builder()
                .email("laborlawseon@gmail.com")
                .mobileNum("010-4000-1278")
                .password(encoder.encode("12345678"))
                .nickname("seonwoo_jung")
                .alarmStatus(ENABLED)
                .memberType(MEMBER)
                .build();
            em.persist(member);
        }

        @Test
        @DisplayName("아이디를 찾는다.")
        void findMember() {
            // given
            String email = memberService.findMemberId(new MemberFindIdCommandRequest(member.getMobileNum(), member.getNickname()));
            assertThat(member.getEmail()).isEqualTo(email);
        }

        @Test
        @DisplayName("비밀번호를 찾는다.")
        void findMemberPassword() {
            // given
            String email = memberService.findMemberPW(new MemberFindPWCommand(member.getMobileNum(), member.getEmail()));
            assertThat(member.getEmail()).isEqualTo(email);
        }
    }
}