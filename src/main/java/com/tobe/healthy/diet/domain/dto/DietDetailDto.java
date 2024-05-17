package com.tobe.healthy.diet.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietDetailDto {

    @Builder.Default
    private Boolean fast = false;
    private DietFileDto dietFile;

}
