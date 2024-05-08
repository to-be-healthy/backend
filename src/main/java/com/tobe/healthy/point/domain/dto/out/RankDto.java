package com.tobe.healthy.point.domain.dto.out;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RankDto {

    private int ranking;
    private int totalMemberCnt;

    @Builder
    public static RankDto create(int ranking, int totalMemberCnt) {
        return RankDto.builder()
                .ranking(ranking)
                .totalMemberCnt(totalMemberCnt)
                .build();
    }
}
