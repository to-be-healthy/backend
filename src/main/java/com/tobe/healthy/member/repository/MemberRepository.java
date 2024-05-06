package com.tobe.healthy.member.repository;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberType;
import com.tobe.healthy.member.domain.entity.SocialType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
	@Query("select m from Member m where m.email = :email and m.socialType = 'NONE' and m.delYn = false")
    Optional<Member> findByEmail(String email);

	@Query("select m from Member m where m.email = :email and m.socialType = :socialType and m.delYn = false")
	Optional<Member> findByEmailAndSocialType(String email, SocialType socialType);

	@Query("select m from Member m where m.userId = :userId and m.name = :name and m.delYn = false")
	Optional<Member> findByUserIdAndName(String userId, String name);

	@Query("select m from Member m where m.userId = :userId and m.memberType = :memberType and m.delYn = false")
	Optional<Member> findByUserId(String userId, MemberType memberType);

	@Query("select m from Member m where m.userId = :userId and m.delYn = false")
	Optional<Member> findByUserId(String userId);

	@Query("select m from Member m where m.email = :email and m.name = :name and m.socialType = 'NONE' and m.delYn = false")
	Optional<Member> findByEmailAndName(String email, String name);

	@Query("select m from Member m where m.id = :memberId and m.delYn = false")
	Optional<Member> findById(Long memberId);

	@EntityGraph(attributePaths = {"gym"})
	Optional<Member> findByIdAndMemberTypeAndDelYnFalse(Long memberId, MemberType memberType);

	@EntityGraph(attributePaths = {"gym", "memberProfile"})
	Optional<Member> findByIdAndDelYnFalse(Long memberId);

	@Query("select m from Member m where m.gym.id = :gymId and m.memberType = 'TRAINER' and m.delYn = false order by m.id desc")
	List<Member> findAllTrainerByGym(@Param("gymId") Long gymId);

	@Query("select m from Member m where m.id in(:members) and m.memberType = 'STUDENT' and m.delYn = false")
	List<Member> findAll(Long[] members);

	@Query("select m from Member m where m.id in(:members) and m.memberType = 'STUDENT' and m.delYn = false")
	List<Member> findAll(List<Long> members);
}
