package com.tobe.healthy.point.domain.dto.out;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class PointDto {

    private String searchDate;
    private int monthPoint;
    private int totalPoint;

    public static PointDto create(String searchDate, int monthPoint, int totalPoint) {
        return PointDto.builder()
                .searchDate(searchDate)
                .monthPoint(monthPoint)
                .totalPoint(totalPoint)
                .build();
    }

}
