package com.tobe.healthy.schedule.presentation;

import com.tobe.healthy.schedule.application.ScheduleService;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleCommand;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule")
@Slf4j
public class ScheduleController {

	private final ScheduleService scheduleService;

	@PostMapping
	public ResponseEntity<LocalDateTime> registerSchedule(ScheduleCommand request) {
		return ResponseEntity.ok(scheduleService.registerSchedule(request));
	}

	@GetMapping
	public ResponseEntity<List<ScheduleCommandResult>> findAllSchedule() {
		return ResponseEntity.ok(scheduleService.findAllSchedule());
	}

	@PatchMapping
	public ResponseEntity<LocalDateTime> modifySchedule(LocalDateTime localDateTime) {
		return ResponseEntity.ok(scheduleService.modifySchedule(localDateTime));
	}
}
