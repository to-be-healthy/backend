package com.tobe.healthy.schedule.domain.dto.out;

import com.querydsl.core.annotations.QueryProjection;
import com.tobe.healthy.schedule.domain.entity.ReserveType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class ScheduleCommandResult {
	private Long id;
	private LocalDateTime startDt;
	private LocalDateTime endDt;
	private ReserveType isReserve;
	private int round;
	private String trainerName;
	private String applicantName;

	@QueryProjection
	public ScheduleCommandResult(Long id, LocalDateTime startDt, LocalDateTime endDt, ReserveType isReserve, int round, String trainerName, String applicantName) {
		this.id = id;
		this.startDt = startDt;
		this.endDt = endDt;
		this.isReserve = isReserve;
		this.round = round;
		this.trainerName = trainerName;
		this.applicantName = applicantName;
	}
}
