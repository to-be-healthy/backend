package com.tobe.healthy.point.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TempRankDto {

    private int ranking;
    private Long memberId;
    private int pointSum;

}
