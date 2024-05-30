package com.tobe.healthy.workout.domain.dto;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberProfile;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class CommentMemberDto {

    private Long memberId;
    private String name;
    private String fileUrl;

    public static CommentMemberDto from(Member member) {
        return CommentMemberDto.builder()
                .memberId(member.getId())
                .name(member.getName())
                .build();
    }

    public static CommentMemberDto create(Member member, MemberProfile memberProfile) {
        CommentMemberDtoBuilder builder = CommentMemberDto.builder()
                .memberId(member.getId())
                .name(member.getName());

        if(memberProfile != null){
            builder.fileUrl(memberProfile.getFileUrl());
        }
        return builder.build();
    }
}
