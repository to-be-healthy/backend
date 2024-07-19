package com.tobe.healthy.member.repository;

import com.tobe.healthy.member.domain.entity.NonMember;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NonMemberRepository extends JpaRepository<NonMember, Long> {

}
