package com.tobe.healthy.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tobe.healthy.member.domain.MemberProfile;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {
}
