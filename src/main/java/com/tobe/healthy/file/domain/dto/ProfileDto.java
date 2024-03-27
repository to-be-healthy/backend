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
    private String filePath;
    private int fileSize;

    public static ProfileDto from(Profile profile){
        return ProfileDto.builder()
                .id(profile.getId())
                .fileName(profile.getFileName())
                .originalName(profile.getOriginalName())
                .extension(profile.getExtension())
                .filePath(profile.getFilePath())
                .fileSize(profile.getFileSize())
                .build();
    }

}
