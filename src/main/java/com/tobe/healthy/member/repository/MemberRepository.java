package com.tobe.healthy.member.repository;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
	@Query("select m from Member m where m.email = :email and m.socialType = 'NONE' and m.delYn = false")
    Optional<Member> findByEmail(@Param("email") String email);

	@Query("select m from Member m where m.email = :email and m.socialType = 'KAKAO' and m.delYn = false")
	Optional<Member> findKakaoByEmailAndSocialType(@Param("email") String email);

	@Query("select m from Member m where m.email = :email and m.socialType = 'NAVER' and m.delYn = false")
	Optional<Member> findNaverByEmailAndSocialType(@Param("email") String email);

	@Query("select m from Member m where m.email = :email and m.socialType = 'GOOGLE' and m.delYn = false")
	Optional<Member> findGoogleByEmailAndSocialType(@Param("email") String email);

	@Query("select m from Member m where m.userId = :userId and m.name = :name and m.delYn = false")
	Optional<Member> findByUserIdAndName(@Param("userId") String userId, @Param("name") String name);

	@Query("select m from Member m where m.userId = :userId and m.memberType = :memberType and m.delYn = false")
	Optional<Member> findByUserId(@Param("userId") String userId, @Param("memberType") MemberType memberType);

	@Query("select m from Member m where m.userId = :userId and m.delYn = false")
	Optional<Member> findByUserId(@Param("userId") String userId);

	@Query("select m from Member m where m.email = :email and m.name = :name and m.socialType = 'NONE' and m.delYn = false")
	Optional<Member> findByEmailAndName(@Param("email") String email, @Param("name") String name);

	@Query("select m from Member m where m.id = :memberId and m.delYn = false")
	Optional<Member> findById(@Param("memberId") Long memberId);

	Optional<Member> findByIdAndMemberTypeAndDelYnFalse(Long memberId, MemberType memberType);

	@Query("select m from Member m where m.gym.id = :gymId and m.memberType = 'TRAINER' and m.delYn = false order by m.id desc")
	Optional<Member> findAllTrainerByGym(@Param("gymId") Long gymId);

	@Query("select m from Member m where m.id in(:members) and m.memberType = 'MEMBER' and m.delYn = false")
	List<Member> findAll(Long[] members);
}
