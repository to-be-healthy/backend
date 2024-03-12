package com.tobe.healthy.schedule.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.schedule.application.ScheduleService;
import com.tobe.healthy.schedule.application.ScheduleService.ScheduleInfo;
import com.tobe.healthy.schedule.domain.dto.in.AutoCreateScheduleCommandRequest;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleCommandRequest;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedule")
@Slf4j
@Tag(name = "schedule", description = "일정 API")
public class ScheduleController {

	private final ScheduleService scheduleService;

	@PostMapping("/create")
	public ResponseHandler<TreeMap<LocalDate, ArrayList<ScheduleInfo>>> createSchedule(@RequestBody @Valid AutoCreateScheduleCommandRequest request) {
		return ResponseHandler.<TreeMap<LocalDate, ArrayList<ScheduleInfo>>>builder()
			.statusCode(HttpStatus.OK)
			.data(scheduleService.autoCreateSchedule(request))
			.message("일정을 생성하였습니다.")
			.build();
	}

	@PostMapping
	public ResponseHandler<Boolean> registerSchedule(@RequestBody ScheduleCommandRequest request) {
		return ResponseHandler.<Boolean>builder()
			.statusCode(HttpStatus.OK)
			.data(scheduleService.registerSchedule(request))
			.message("일정 등록이 완료되었습니다.")
			.build();
	}

	@GetMapping
	public ResponseHandler<List<ScheduleCommandResult>> findAllSchedule() {
		return ResponseHandler.<List<ScheduleCommandResult>>builder()
			.statusCode(HttpStatus.OK)
			.data(scheduleService.findAllSchedule())
			.message("전체 일정을 조회했습니다.")
			.build();
	}

	@PatchMapping("/{scheduleId}")
	public ResponseHandler<Boolean> cancelSchedule(@PathVariable Long scheduleId) {
		return ResponseHandler.<Boolean>builder()
			.statusCode(HttpStatus.OK)
			.data(scheduleService.cancelSchedule(scheduleId))
			.message("일정을 취소하였습니다.")
			.build();
	}
}