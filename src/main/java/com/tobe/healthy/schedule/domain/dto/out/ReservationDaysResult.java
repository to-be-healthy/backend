package com.tobe.healthy.schedule.domain.dto.out;

import lombok.Builder;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Data
@Builder
public class ReservationDaysResult {

    @Builder.Default
    private List<String> reservationDays = null;

    public static ReservationDaysResult from(List<String> days) {
        return ReservationDaysResult.builder()
                .reservationDays(ObjectUtils.isEmpty(days) ? null : days)
                .build();
    }
}
