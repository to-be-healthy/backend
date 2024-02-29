package com.tobe.healthy.schedule.domain.dto.in;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleCommand {
	private LocalDateTime startDate;
	private String round;
	private String trainerEmail;
	private String applicantEmail;
}
