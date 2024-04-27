package com.tobe.healthy.workout.domain.dto;

import com.tobe.healthy.file.domain.entity.Profile;
import com.tobe.healthy.member.domain.entity.Member;
import lombok.Builder;
import lombok.Data;

@Data
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

    public static CommentMemberDto create(Member member, Profile profile) {
        return CommentMemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .fileUrl(profile.getFileUrl())
                .build();
    }
}