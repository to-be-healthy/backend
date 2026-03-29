package com.tobe.healthy.push.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tobe.healthy.push.domain.entity.MemberToken;

public interface MemberTokenRepository extends JpaRepository<MemberToken, Long> {

	Optional<MemberToken> findByMemberId(Long memberId);
}
