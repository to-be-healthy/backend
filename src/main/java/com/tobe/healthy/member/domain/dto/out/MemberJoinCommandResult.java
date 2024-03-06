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
    private String nickname;
    private String mobileNum;
    private MemberType category;

    public static MemberJoinCommandResult of(Member member){
        return MemberJoinCommandResult.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .category(member.getMemberType())
                .mobileNum(member.getMobileNum())
                .build();
    }
}
