package com.tobe.healthy.point.domain.dto.out;

import com.tobe.healthy.point.domain.dto.PointDto;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PointGetResult {

    private int monthPoint;
    private int totalPoint;

    @Builder.Default
    private List<PointDto> pointHistories = null;

    public static PointGetResult create(int monthPoint, int totalPoint, List<PointDto> pointHistoryDtos) {
        return PointGetResult.builder()
                .monthPoint(monthPoint)
                .totalPoint(totalPoint)
                .pointHistories(pointHistoryDtos)
                .build();
    }

}
