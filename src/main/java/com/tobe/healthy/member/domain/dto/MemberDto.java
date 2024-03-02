package com.tobe.healthy.member.domain.dto;

import com.tobe.healthy.member.domain.entity.Member;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class MemberDto {

    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String mobileNum;

    public static MemberDto from(Member member){
        return MemberDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .nickname(member.getNickname())
                .mobileNum(member.getMobileNum())
                .build();
    }

}