package com.tobe.healthy.point.domain.dto.out;

import com.tobe.healthy.point.domain.dto.PointHistoryDto;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@Builder
public class PointDto {

    private String searchDate;
    private int monthPoint;
    private int totalPoint;

    @Builder.Default
    private List<PointHistoryDto> pointHistories = null;

    public static PointDto create(String searchDate, int monthPoint, int totalPoint) {
        return PointDto.builder()
                .searchDate(searchDate)
                .monthPoint(monthPoint)
                .totalPoint(totalPoint)
                .build();
    }

    public static PointDto create(String searchDate, int monthPoint, int totalPoint, List<PointHistoryDto> pointHistoryDtos) {
        return PointDto.builder()
                .searchDate(searchDate)
                .monthPoint(monthPoint)
                .totalPoint(totalPoint)
                .pointHistories(pointHistoryDtos.isEmpty() ? null : pointHistoryDtos)
                .build();
    }

}
