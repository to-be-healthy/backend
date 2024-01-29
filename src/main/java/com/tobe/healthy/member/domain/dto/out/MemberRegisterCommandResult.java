package com.tobe.healthy.member.domain.dto.out;

import com.tobe.healthy.member.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberRegisterCommandResult {
    private long memberId;
    private String email;
    private String nickname;

    public static MemberRegisterCommandResult from(Member member){
        return new MemberRegisterCommandResult(member.getId(), member.getEmail(), member.getNickname());
    }
}
