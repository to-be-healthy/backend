package com.tobe.healthy.schedule.presentation;

import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResponse;
import com.tobe.healthy.schedule.application.ScheduleService;
import com.tobe.healthy.schedule.domain.dto.in.AutoCreateScheduleCommandRequest;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleCommandRequest;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule")
@Slf4j
public class ScheduleController {

	private final ScheduleService scheduleService;

	@PostMapping("/auto-create")
	public ResponseEntity<List<ScheduleCommandResponse>> autoCreateSchedule(@RequestBody AutoCreateScheduleCommandRequest request) {
		return ResponseEntity.ok(scheduleService.autoCreateSchedule(request));
	}

	@PostMapping
	public ResponseEntity<Boolean> registerSchedule(@RequestBody ScheduleCommandRequest request) {
		return ResponseEntity.ok(scheduleService.registerSchedule(request));
	}

	@GetMapping
	public ResponseEntity<List<ScheduleCommandResult>> findAllSchedule() {
		return ResponseEntity.ok(scheduleService.findAllSchedule());
	}

	@PatchMapping("/{scheduleId}")
	public ResponseEntity<Boolean> cancelSchedule(@PathVariable Long scheduleId) {
		return ResponseEntity.ok(scheduleService.cancelSchedule(scheduleId));
	}
}
