package com.tobe.healthy.diet.domain.dto;

import com.tobe.healthy.diet.domain.entity.DietFiles;
import com.tobe.healthy.diet.domain.entity.DietType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DietFileDto {

    private Long id;
    private Long dietId;
    private String fileUrl;
    private DietType type;

    public static DietFileDto from(DietFiles dietFile) {
        return DietFileDto.builder()
                .id(dietFile.getId())
                .fileUrl(dietFile.getFileUrl())
                .type(dietFile.getType())
                .dietId(dietFile.getDiet().getDietId())
                .build();
    }
}
