package com.tobe.healthy.point.domain.dto;

import com.tobe.healthy.point.domain.entity.Calculation;
import com.tobe.healthy.point.domain.entity.Point;
import com.tobe.healthy.point.domain.entity.PointType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointDto {

    private Long pointId;
    private PointType type;
    private Calculation calculation;
    private int point;
    private LocalDateTime createdAt;

    public static PointDto from(Point point) {
        return PointDto.builder()
                .pointId(point.getPointId())
                .type(point.getType())
                .calculation(point.getCalculation())
                .point(point.getPoint())
                .createdAt(point.getCreatedAt())
                .build();
    }

}
