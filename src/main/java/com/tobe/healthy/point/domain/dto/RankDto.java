package com.tobe.healthy.point.domain.dto;

import jakarta.annotation.security.DenyAll;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DenyAll
public class RankDto {

    private int ranking;
    private Long memberId;
    private int pointSum;

}
