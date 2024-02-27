package com.tobe.healthy.member.repository;

import com.tobe.healthy.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByNickname(String nickname);
	Optional<Member> findByMobileNumAndEmail(String mobileNum, String email);
	Optional<Member> findByMobileNumAndNickname(String mobileNum, String nickname);
}
