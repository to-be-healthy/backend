package com.tobe.healthy.member.application;

import static com.tobe.healthy.config.error.ErrorCode.MEMBER_DUPLICATION_EMAIL;
import static com.tobe.healthy.config.error.ErrorCode.MEMBER_DUPLICATION_NICKNAME;
import static com.tobe.healthy.member.domain.entity.Alarm.ABLE;
import static com.tobe.healthy.member.domain.entity.MemberCategory.MEMBER;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.member.domain.entity.Gym;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @Rollback(false)
    @DisplayName("회원을 등록한다.")
    void registerMember() {
        Gym gym = Gym.builder()
            .name("쏘마휘트니스")
            .build();

        Member entity = Member.builder()
                .email("laborlawseon@gmail.com")
                .password("12345678")
                .nickname("seonwoo_jung")
                .isAlarm(ABLE)
                .category(MEMBER)
                .gym(gym)
                .build();

        em.persist(entity);

        Member findMember = em.find(Member.class, 1L);
        log.info("findMember => {}", findMember);
    }

//    @Test
//    void validateDuplicateEmailTest() {
//        assertThatThrownBy(() -> validateDuplicateEmail()).hasMessage(MEMBER_DUPLICATION_EMAIL.getMessage());
//    }

//    @Test
//    void validateDuplicateNicknameTest() {
//        assertThatThrownBy(() -> validateDuplicateNickname()).hasMessage(MEMBER_DUPLICATION_NICKNAME.getMessage());
//    }

    void validateDuplicateEmail() {
        registerMember();
        memberRepository.findByEmail("laborlawseon@gmail.com").ifPresent(x -> {
            throw new CustomException(MEMBER_DUPLICATION_EMAIL);
        });
    }

    void validateDuplicateNickname() {
        registerMember();
        memberRepository.findByNickname("seonwoo_jung").ifPresent(x -> {
            throw new CustomException(MEMBER_DUPLICATION_NICKNAME);
        });
    }

    @Test
    void registerEmail() {
        // given
        String email = UUID.randomUUID().toString() + "@kakao.com";
        log.info(email);
        // when


        // then
//        assertThat();

    }
}