package com.tobe.healthy.member.repository;

import com.tobe.healthy.member.domain.entity.BearerToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BearerTokenRepository extends JpaRepository<BearerToken, Long> {
    Optional<BearerToken> findByRefreshToken(String refreshToken);
    Optional<BearerToken> findByAccessToken(String accessToken);

    @Modifying
    @Query(value = "delete from BearerToken rt where rt.memberId = :memberId")
    void deleteAllByMemberId(@Param("memberId") Long memberId);
}
