package com.tobe.healthy.member.domain.dto.out;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MemberJoinCommandResult {
    private String email;
    private String userId;
    private String name;
    private MemberType memberType;

    public static MemberJoinCommandResult of(Member member){
        return MemberJoinCommandResult.builder()
                .email(member.getEmail())
                .userId(member.getUserId())
                .name(member.getName())
                .memberType(member.getMemberType())
                .build();
    }
}
