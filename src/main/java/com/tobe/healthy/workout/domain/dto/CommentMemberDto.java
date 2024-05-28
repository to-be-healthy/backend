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

    private Long id;
    private String name;
    private String fileUrl;

    public static CommentMemberDto from(Member member) {
        return CommentMemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .build();
    }

    public static CommentMemberDto create(Member member, MemberProfile memberProfile) {
        return CommentMemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .fileUrl(memberProfile.getFileUrl())
                .build();
    }
}
