package com.tobe.healthy.member.repository;

import com.tobe.healthy.member.domain.entity.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {
}
