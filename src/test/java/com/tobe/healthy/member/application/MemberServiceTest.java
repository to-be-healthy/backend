package com.tobe.healthy.member.application;

import static com.tobe.healthy.member.domain.entity.Alarm.ABLE;
import static com.tobe.healthy.member.domain.entity.MemberCategory.MEMBER;
import static org.assertj.core.api.Assertions.assertThat;

import com.tobe.healthy.common.RedisService;
import com.tobe.healthy.file.application.FileService;
import com.tobe.healthy.member.domain.dto.in.VerifyAuthMailRequest;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import java.io.FileInputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
@Nested
@DisplayName("회원 기능 테스트")
@AutoConfigureMockMvc
class MemberServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RedisService redisService;

    @Autowired
    private FileService fileService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MockMvc mvc;

    @Nested
    @DisplayName("회원가입")
    class Join {

        @Test
        @DisplayName("이메일 인증을 받는다.")
        void sendAuth() {
            String email = "laborlawseon@gmail.com";
            String sendEmail = memberService.sendAuthMail(email);
            assertThat(email).isEqualTo(sendEmail);
        }

        @Test
        @DisplayName("이메일 인증번호를 검증한다.")
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
                .isAlarm(ABLE)
                .category(MEMBER)
                .build();

            em.persist(member);

            Member findMember = em.find(Member.class, 1L);
            assertThat(member).isEqualTo(findMember);
        }

        @Test
        @DisplayName("프로필 사진을 등록한다.")
        void registerProfile() throws IOException {
            // given
            Member member = Member.builder()
                .email("laborlawseon@gmail.com")
                .password("12345678")
                .nickname("seonwoo_jung")
                .isAlarm(ABLE)
                .category(MEMBER)
                .build();

            em.persist(member);

            MockMultipartFile image1 = new MockMultipartFile(
                "img_640x640.jpg",
                "img_640x640" + "." + "jpg",
                "jpg",
                new FileInputStream("upload/img_640x640.jpg"));

            fileService.uploadFile(image1, member.getId());
        }
    }

    @Nested
    @DisplayName("로그인 및 토큰 갱신")
    class Login {

    }

    @Nested
    @DisplayName("아이디, 비밀번호 찾기")
    class find {

    }
}