package com.tobe.healthy.schedule.application;

import static com.tobe.healthy.config.error.ErrorCode.SCHEDULE_NOT_FOUND;
import static java.util.stream.Collectors.toList;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleCommand;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.repository.ScheduleRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ScheduleService {

	private final ScheduleRepository scheduleRepository;

	public LocalDateTime registerSchedule(ScheduleCommand request) {
		return scheduleRepository.registerSchedule(request);
	}

	public List<ScheduleCommandResult> findAllSchedule() {
		return scheduleRepository.findAll()
			.stream()
			.map(ScheduleCommandResult::of)
			.collect(toList());
	}

	public LocalDateTime modifySchedule(LocalDateTime localDateTime) {
		Schedule entity = scheduleRepository.findByStartDate(localDateTime)
			.orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));
		entity.cancelSchedule();
		return entity.getStartDate();
	}
}
