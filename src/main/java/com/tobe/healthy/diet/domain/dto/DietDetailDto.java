package com.tobe.healthy.diet.domain.dto;

import lombok.*;


@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietDetailDto {

    @Builder.Default
    private Boolean fast = false;
    private DietFileDto dietFile;

    public DietDetailDto(Boolean fast) {
        this.fast = fast;
    }

}
