package com.tobe.healthy.member.domain.dto;


import com.tobe.healthy.member.domain.entity.MemberProfile;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
@Builder
public class ProfileDto {

    private Long id;
    private String fileUrl;

    public static ProfileDto from(MemberProfile memberProfile){
        return ProfileDto.builder()
                .id(memberProfile.getId())
                .fileUrl(memberProfile.getFileUrl())
                .build();
    }
}
