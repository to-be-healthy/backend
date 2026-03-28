package com.tobe.healthy.schedule.domain.dto.out;

import java.util.List;

import org.springframework.util.ObjectUtils;

import lombok.Builder;
import lombok.Data;

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
