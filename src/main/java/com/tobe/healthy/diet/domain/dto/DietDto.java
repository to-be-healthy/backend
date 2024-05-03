package com.tobe.healthy.diet.domain.dto;

import com.tobe.healthy.diet.domain.entity.Diet;
import com.tobe.healthy.member.domain.dto.MemberDto;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class DietDto {

    private Long dietId;
    private MemberDto member;
    private Long likeCnt;
    private Long commentCnt;

    @Builder.Default
    private Boolean fastBreakfast = false;

    @Builder.Default
    private Boolean fastLunch = false;

    @Builder.Default
    private Boolean fastDinner = false;

    @Builder.Default
    private List<DietFileDto> dietFiles = new ArrayList<>();

    public static DietDto create(Long dietId, List<DietFileDto> dietFiles) {
        return DietDto.builder()
                .dietId(dietId)
                .dietFiles(dietFiles)
                .build();
    }

    public static DietDto from(Diet diet) {
        return DietDto.builder()
                .dietId(diet.getDietId())
                .member(MemberDto.from(diet.getMember()))
                .likeCnt(diet.getLikeCnt())
                .commentCnt(diet.getCommentCnt())
                .fastBreakfast(diet.getFastBreakfast())
                .fastLunch(diet.getFastLunch())
                .fastDinner(diet.getFastDinner())
                .build();
    }
}
