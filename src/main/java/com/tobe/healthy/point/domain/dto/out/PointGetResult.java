package com.tobe.healthy.point.domain.dto.out;

import com.tobe.healthy.point.domain.dto.PointDto;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class PointGetResult {

    private int point;
    @Builder.Default
    private List<PointDto> pointHistories = null;

    public static PointGetResult create(int point, List<PointDto> pointHistoryDtos) {
        return PointGetResult.builder()
                .point(point)
                .pointHistories(pointHistoryDtos)
                .build();
    }

}
