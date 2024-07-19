package com.tobe.healthy.member.repository;

import com.tobe.healthy.member.domain.entity.NonMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface NonMemberRepository extends JpaRepository<NonMember, Long> {

    Optional<NonMember> findByInvitationLink(String invitationLink);
}
