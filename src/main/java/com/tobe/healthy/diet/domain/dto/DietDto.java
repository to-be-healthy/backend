package com.tobe.healthy.diet.domain.dto;

import com.tobe.healthy.file.domain.dto.DietFileDto;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class DietDto {

    private Long dietId;

    @Builder.Default
    private List<DietFileDto> dietFiles = new ArrayList<>();

    public static DietDto create(Long dietId, List<DietFileDto> dietFiles) {
        return DietDto.builder()
                .dietId(dietId)
                .dietFiles(dietFiles)
                .build();
    }
}
