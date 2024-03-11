package com.tobe.healthy.member.repository;

import com.tobe.healthy.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
	@Query("select m from Member m where m.email = :email and m.delYn = 'N'")
    Optional<Member> findByEmail(String email);

	@Query("select m from Member m where m.userId = :userId and m.name = :name and m.delYn = 'N'")
	Optional<Member> findByUserIdAndName(String userId, String name);

	@Query("select m from Member m where m.userId = :userId and m.delYn = 'N'")
	Optional<Member> findByUserId(String userId);

	@Query("select m from Member m where m.email = :email and m.name = :name and m.delYn = 'N'")
	Optional<Member> findByEmailAndName(String email, String name);
}
