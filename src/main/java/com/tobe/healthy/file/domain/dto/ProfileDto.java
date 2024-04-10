package com.tobe.healthy.file.domain.dto;


import com.tobe.healthy.file.domain.entity.Profile;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ProfileDto {

    private Long id;
    private String fileName;
    private String originalName;
    private String extension;
    private int fileSize;
    private String fileUrl;

    public static ProfileDto from(Profile profile){
        return ProfileDto.builder()
                .id(profile.getId())
                .fileName(profile.getFileName())
                .originalName(profile.getOriginalName())
                .extension(profile.getExtension())
                .fileSize(profile.getFileSize())
                .fileUrl(profile.getFileUrl())
                .build();
    }

}
