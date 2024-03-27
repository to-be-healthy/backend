package com.tobe.healthy.member.repository;

import com.tobe.healthy.member.domain.entity.Member;


public interface MemberRepositoryCustom {

    Member findByMemberIdWithGym(Long trainerId);
    Member findByMemberIdWithProfile(Long memberId);

}
