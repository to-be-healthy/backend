package com.tobe.healthy.member.domain.dto;


import com.tobe.healthy.member.domain.entity.MemberProfile;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.util.ObjectUtils;


@Data
@ToString
@Builder
public class ProfileDto {

    private Long id;
    private String fileUrl;

    public static ProfileDto from(MemberProfile memberProfile) {
        if (ObjectUtils.isEmpty(memberProfile)) {
            return null;
        }
        return ProfileDto.builder()
                .id(memberProfile.getId())
                .fileUrl(memberProfile.getFileUrl())
                .build();
    }
}
