package com.tobe.healthy.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tobe.healthy.member.domain.entity.NonMember;

public interface NonMemberRepository extends JpaRepository<NonMember, Long> {

	Optional<NonMember> findByInvitationLink(String invitationLink);
}
