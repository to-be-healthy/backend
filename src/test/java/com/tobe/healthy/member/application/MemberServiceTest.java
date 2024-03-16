package com.tobe.healthy.member.application;

import static com.tobe.healthy.config.error.ErrorCode.MEMBER_NOT_FOUND;
import static com.tobe.healthy.member.domain.entity.AlarmStatus.ENABLED;
import static com.tobe.healthy.member.domain.entity.MemberType.MEMBER;
import static org.assertj.core.api.Assertions.assertThat;

import com.tobe.healthy.common.RedisService;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.config.security.JwtTokenProvider;
import com.tobe.healthy.file.application.FileService;
import com.tobe.healthy.member.domain.dto.in.MemberFindIdCommand;
import com.tobe.healthy.member.domain.dto.in.MemberFindPWCommand;
import com.tobe.healthy.member.domain.dto.in.MemberLoginCommand;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.Tokens;
import com.tobe.healthy.member.repository.MemberRepository;
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
    private MemberRepository memberRepository;

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

            assertThat(memberService.verifyEmailAuthNumber(authKey, email)).isTrue();
        }

        @Test
        @DisplayName("회원가입을 한다.")
        void registerMember() {
            Member member = Member.builder()
                .email("laborlawseon@gmail.com")
                .password("12345678")
                .userId("seonwoo_jung")
                .name("정선우")
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
                .userId("seonwoo_jung")
                .name("정선우")
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

        @Autowired
        private JwtTokenProvider tokenProvider;

        @BeforeEach
        @DisplayName("회원을 등록한다.")
		void setup() {
            member = Member.builder()
                .email("laborlawseon@gmail.com")
                .password(encoder.encode("12345678"))
                .userId("seonwoo_jung")
                .name("정선우")
                .alarmStatus(ENABLED)
                .memberType(MEMBER)
                .build();
            em.persist(member);
        }

        @Test
        @DisplayName("로그인을 진행한다.")
        void login() {
            Tokens tokens = memberService.login(new MemberLoginCommand(member.getUserId(), "12345678"));
            log.info("tokens => {}", tokens);
        }

        /**
         *  총 반복횟수: {totalRepetitions}
         *  현재 반복횟수: {currentRepetition}
         */
//        @RepeatedTest(name = "{currentRepetition}번 째 토큰 갱신", value = 100)
        @Test
        @DisplayName("토큰을 갱신한다.")
        void refreshToken() {
            Tokens before = memberService.login(new MemberLoginCommand(member.getUserId(), "12345678"));
            Tokens after = memberService.refreshToken(member.getUserId(), before.getRefreshToken());
            assertThat(before.getAccessToken()).isNotEqualTo(after.getAccessToken());
        }
    }

    @Nested
    @DisplayName("아이디/비밀번호 찾기, 회원탈퇴")
    class MemberFind {

        private Member member;

        @BeforeEach
        @DisplayName("회원을 등록한다.")
        void setup() {
            member = Member.builder()
                .email("laborlawseon@gmail.com")
                .userId("laborlawseon")
                .password(encoder.encode("12345678"))
                .name("seonwoo_jung")
                .alarmStatus(ENABLED)
                .memberType(MEMBER)
                .build();
            em.persist(member);
        }

        @Test
        @DisplayName("아이디를 찾는다.")
        void findMember() {
            // given
            String userId = memberService.findUserId(new MemberFindIdCommand(member.getEmail(), member.getName()));
            assertThat(member.getUserId()).isEqualTo(userId);
        }

        @Test
        @DisplayName("비밀번호를 찾는다.")
        void findMemberPassword() {
            // given
            String email = memberService.findMemberPW(new MemberFindPWCommand(member.getUserId(), member.getName()));
            assertThat(member.getEmail()).isEqualTo(email);
        }

        @Test
        @DisplayName("회원탈퇴 한다.")
        void withdrawalAccount() {
            Member member = memberRepository.findByUserId("laborlawseon")
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
            member.deleteMember();
            em.flush();
            em.clear();
            Member findMember = em.createQuery(
                    "select m from Member m where m.userId = :userId and delYn = 'Y'", Member.class)
                .setParameter("userId", member.getUserId()).getSingleResult();
            assertThat(findMember.isDelYn()).isEqualTo(true);
        }
    }
}