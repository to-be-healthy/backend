package com.tobe.healthy.diet.domain.dto;

import com.tobe.healthy.diet.domain.entity.Diet;
import com.tobe.healthy.member.domain.dto.MemberDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

import static com.tobe.healthy.diet.domain.entity.DietType.BREAKFAST;

@Data
@Builder
public class DietDto {

    private Long dietId;
    private MemberDto member;
    private Long likeCnt;
    private Long commentCnt;

    @Builder.Default
    private DietDetailDto breakfast = new DietDetailDto();
    @Builder.Default
    private DietDetailDto lunch = new DietDetailDto();
    @Builder.Default
    private DietDetailDto dinner = new DietDetailDto();


    public static DietDto create(Long dietId, List<DietFileDto> dietFiles) {
        DietDto dto = DietDto.builder()
                .dietId(dietId)
                .build();
        dto.setDietFiles(dietFiles);
        return dto;
    }

    public static DietDto from(Diet diet) {
        DietDto dto = DietDto.builder()
                .dietId(diet.getDietId())
                .member(MemberDto.from(diet.getMember()))
                .likeCnt(diet.getLikeCnt())
                .commentCnt(diet.getCommentCnt())
                .build();
        dto.breakfast.setFast(diet.getFastBreakfast());
        dto.lunch.setFast(diet.getFastLunch());
        dto.dinner.setFast(diet.getFastDinner());
        return dto;
    }

    public void setDietFiles(List<DietFileDto> filesDto) {
        for(DietFileDto file : filesDto){
            switch (file.getType()){
                case BREAKFAST -> this.breakfast.setDietFile(file);
                case LUNCH -> this.lunch.setDietFile(file);
                case DINNER -> this.dinner.setDietFile(file);
            }
        }
    }

}
