package com.tobe.healthy.member.repository;

import com.tobe.healthy.member.domain.dto.out.MemberDetailResult;
import com.tobe.healthy.member.domain.dto.out.MemberInTeamResult;
import com.tobe.healthy.member.domain.entity.Member;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;


public interface MemberRepositoryCustom {
    Member findByMemberIdWithGym(Long trainerId);
    Member findByMemberIdWithProfileAndGym(Long memberId);
    List<MemberInTeamResult> findAllMyMemberInTeam(Long trainerId, String searchValue, String sortValue, Pageable pageable);
    Page<Member> findAllUnattachedMembers(Long gymId, String searchValue, String sortValue, Pageable pageable);
    MemberDetailResult getMemberOfTrainer(Long memberId);
    List<Member> findAllTrainerByGym(@Param("gymId") Long gymId);
}
