package com.tobe.healthy.member.repository;

import com.tobe.healthy.gym.domain.dto.MemberInTeamDto;
import com.tobe.healthy.member.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface MemberRepositoryCustom {

    Member findByMemberIdWithGym(Long trainerId);
    Member findByMemberIdWithProfile(Long memberId);
    Page<MemberInTeamDto> findAllMyMemberInTeam(Long trainerId, String searchValue, String sortValue, Pageable pageable);

}
