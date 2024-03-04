package com.tobe.healthy.member.domain.dto.out;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberCategory;
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
    private MemberCategory category;

    public static MemberJoinCommandResult of(Member member){
        return MemberJoinCommandResult.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .category(member.getCategory())
                .mobileNum(member.getMobileNum())
                .build();
    }
}
