package com.tobe.healthy.diet.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tobe.healthy.diet.domain.entity.Diet;
import com.tobe.healthy.file.domain.dto.DietFileDto;
import com.tobe.healthy.file.domain.dto.WorkoutHistoryFileDto;
import com.tobe.healthy.member.domain.dto.MemberDto;
import com.tobe.healthy.workout.domain.dto.CompletedExerciseDto;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryDto;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class DietDto {

    private Long dietId;
    private MemberDto member;
    private Long likeCnt;

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
                .fastBreakfast(diet.getFastBreakfast())
                .fastLunch(diet.getFastLunch())
                .fastDinner(diet.getFastDinner())
                .build();
    }
}
