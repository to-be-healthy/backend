package com.tobe.healthy.member.domain.dto;

import com.tobe.healthy.member.domain.entity.Member;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class MemberDto {

    private Long id;
    private String userId;
    private String email;
    private String name;
    private char delYn;

    public static MemberDto from(Member member){
        return MemberDto.builder()
                .id(member.getId())
                .userId(member.getUserId())
                .email(member.getEmail())
                .name(member.getName())
                .delYn(member.getDelYn())
                .build();
    }

}