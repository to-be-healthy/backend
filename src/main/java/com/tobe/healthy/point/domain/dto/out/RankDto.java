package com.tobe.healthy.point.domain.dto.out;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RankDto {

    private int ranking;
    private int lastMonthRanking;
    private int totalMemberCnt;

}
