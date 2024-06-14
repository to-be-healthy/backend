package com.tobe.healthy.diet.domain.dto;

import lombok.*;

import static com.tobe.healthy.common.Utils.THUMB_PREFIX;

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

    public void setDietFileToThumnail(DietFileDto file) {
        file.setFileUrl(file.getFileUrl().replace("amazonaws.com/diet/", "amazonaws.com/diet/" + THUMB_PREFIX));
        this.dietFile = file;
    }
}
