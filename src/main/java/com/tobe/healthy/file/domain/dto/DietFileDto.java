package com.tobe.healthy.file.domain.dto;

import com.tobe.healthy.file.domain.entity.DietFile;
import com.tobe.healthy.file.domain.entity.DietType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DietFileDto {

    private Long id;
    private Long dietId;
    private String fileName;
    private String originalName;
    private String extension;
    private Long fileSize;
    private String fileUrl;
    private DietType type;

    public static DietFileDto from(DietFile dietFile) {
        return DietFileDto.builder()
                .id(dietFile.getId())
                .fileName(dietFile.getFileName())
                .originalName(dietFile.getOriginalName())
                .extension(dietFile.getExtension())
                .fileSize(dietFile.getFileSize())
                .fileUrl(dietFile.getFileUrl())
                .type(dietFile.getType())
                .dietId(dietFile.getDiet().getDietId())
                .build();
    }
}
