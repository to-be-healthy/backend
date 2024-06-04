package com.tobe.healthy.member.domain.dto.out;

import com.tobe.healthy.member.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CommandAssignNicknameResult {

    private Long memberId;
    private String nickname;

    public static CommandAssignNicknameResult from(Member member) {
        return CommandAssignNicknameResult.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .build();
    }
}
