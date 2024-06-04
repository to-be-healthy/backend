package com.tobe.healthy.member.domain.dto.out;

import com.tobe.healthy.member.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CommandChangeNameResult {
    private Long memberId;
    private String name;

    public static CommandChangeNameResult from(Member member) {
        return CommandChangeNameResult.builder()
                .memberId(member.getId())
                .name(member.getName())
                .build();
    }
}
