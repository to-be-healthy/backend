package com.tobe.healthy.member.repository;

import com.tobe.healthy.gym.domain.dto.MemberInTeamDto;
import com.tobe.healthy.member.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface MemberRepositoryCustom {

    Member findByMemberIdWithGym(Long trainerId);
    Member findByMemberIdWithProfile(Long memberId);
    List<MemberInTeamDto> findAllMyMemberInTeam(Long trainerId, String searchValue, String sortValue, Pageable pageable);

    Page<Member> findAllUnattachedMembers(String searchValue, String sortValue, Pageable pageable);
}
