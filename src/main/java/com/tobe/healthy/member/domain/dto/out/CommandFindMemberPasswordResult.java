package com.tobe.healthy.member.domain.dto.out;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.SocialType;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class CommandFindMemberPasswordResult {
    @JsonIgnore
    private String email;
    @JsonIgnore
    private String message;
    private SocialType socialType;

    public static CommandFindMemberPasswordResult from(Member member, String message) {
        return CommandFindMemberPasswordResult.builder()
                .email(member.getEmail())
                .message(message)
                .socialType(member.getSocialType())
                .build();
    }
}
