package com.tobe.healthy.push.repository;

import com.tobe.healthy.push.domain.entity.MemberToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberTokenRepository extends JpaRepository<MemberToken, Long> {

    Optional<MemberToken> findByMemberId(Long memberId);
}
