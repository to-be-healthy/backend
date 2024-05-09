package com.tobe.healthy.point.domain.dto;

import com.tobe.healthy.point.domain.entity.Calculation;
import com.tobe.healthy.point.domain.entity.Point;
import com.tobe.healthy.point.domain.entity.PointType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryDto {

    private Long pointId;
    private PointType type;
    private Calculation calculation;
    private int point;
    private LocalDateTime createdAt;

    public static PointHistoryDto from(Point point) {
        return PointHistoryDto.builder()
                .pointId(point.getPointId())
                .type(point.getType())
                .calculation(point.getCalculation())
                .point(point.getPoint())
                .createdAt(point.getCreatedAt())
                .build();
    }

}
