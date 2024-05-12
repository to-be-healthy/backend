package com.tobe.healthy.member.repository;

import com.tobe.healthy.member.domain.dto.out.MemberDetailResult;
import com.tobe.healthy.member.domain.dto.out.MemberInTeamResult;
import com.tobe.healthy.member.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface MemberRepositoryCustom {

    Member findByMemberIdWithGym(Long mmeberId);
    Member findByMemberIdWithProfileAndGym(Long memberId);
    List<MemberInTeamResult> findAllMyMemberInTeam(Long trainerId, String searchValue, String sortValue, Pageable pageable);
    Page<Member> findAllUnattachedMembers(Long gymId, String searchValue, String sortValue, Pageable pageable);
    MemberDetailResult getMemberOfTrainer(Long memberId);
    List<Member> findAllTrainerByGym(@Param("gymId") Long gymId);
    List<MemberInTeamResult> getBestStudent(Long trainerId);
    Optional<Member> findMemberById(Long memberId);
}
