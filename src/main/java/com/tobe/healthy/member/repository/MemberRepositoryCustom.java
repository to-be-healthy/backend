package com.tobe.healthy.member.repository;

import com.querydsl.core.Tuple;
import com.tobe.healthy.member.domain.entity.Member;

import java.util.List;


public interface MemberRepositoryCustom {

    Member findByMemberIdWithGym(Long trainerId);
    Member findByMemberIdWithProfile(Long memberId);
    List<Tuple> findAllMyMemberInTrainer(Long trainerId);

}
