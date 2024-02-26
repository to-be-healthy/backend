package com.tobe.healthy.member.domain.dto.out;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MemberRegisterCommandResult {
    private String email;
    private String nickname;
    private MemberCategory category;

    public static MemberRegisterCommandResult of(Member member){
        return MemberRegisterCommandResult.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .category(member.getCategory())
                .build();
    }
}
