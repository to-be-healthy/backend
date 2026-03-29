package com.tobe.healthy.schedule.application;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tobe.healthy.common.LessonTimeFormatter;
import com.tobe.healthy.common.error.CustomException;
import com.tobe.healthy.common.error.ErrorCode;
import com.tobe.healthy.common.event.CustomEventPublisher;
import com.tobe.healthy.common.event.EventType;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.notification.presentation.dto.in.CommandSendNotification;
import com.tobe.healthy.notification.domain.entity.NotificationCategory;
import com.tobe.healthy.notification.domain.entity.NotificationType;
import com.tobe.healthy.schedule.presentation.dto.in.CommandRegisterDefaultLessonTime;
import com.tobe.healthy.schedule.presentation.dto.in.CommandRegisterSchedule;
import com.tobe.healthy.schedule.presentation.dto.in.CommandUpdateScheduleStatus;
import com.tobe.healthy.schedule.presentation.dto.out.CommandCancelStudentReservationResult;
import com.tobe.healthy.schedule.presentation.dto.out.CommandRegisterDefaultLessonTimeResult;
import com.tobe.healthy.schedule.presentation.dto.out.CommandRegisterScheduleByStudentResult;
import com.tobe.healthy.schedule.presentation.dto.out.CommandRegisterScheduleResult;
import com.tobe.healthy.schedule.presentation.dto.out.CommandScheduleStatusResult;
import com.tobe.healthy.schedule.presentation.dto.out.ScheduleIdInfo;
import com.tobe.healthy.schedule.domain.entity.ReservationStatus;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.domain.entity.TrainerScheduleClosedDaysInfo;
import com.tobe.healthy.schedule.domain.entity.TrainerScheduleInfo;
import com.tobe.healthy.schedule.repository.TrainerScheduleInfoRepository;
import com.tobe.healthy.schedule.repository.TrainerScheduleRepository;
import com.tobe.healthy.schedule.repository.waiting.ScheduleWaitingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TrainerScheduleCommandService {

	public static final long ONE_DAY = 1L;

	private final MemberRepository memberRepository;
	private final TrainerScheduleRepository trainerScheduleRepository;
	private final TrainerScheduleInfoRepository trainerScheduleInfoRepository;
	private final ScheduleWaitingRepository scheduleWaitingRepository;
	private final CustomEventPublisher<CommandSendNotification> notificationPublisher;
	private final CustomEventPublisher<Long> eventPublisher;

	public CommandRegisterDefaultLessonTimeResult registerDefaultLessonTime(
		CommandRegisterDefaultLessonTime request,
		Long trainerId
	) {
		Member findTrainer = findMemberById(trainerId);

		TrainerScheduleInfo existing = trainerScheduleInfoRepository.findOneByTrainerId(trainerId);
		if (existing != null) {
			List<TrainerScheduleClosedDaysInfo> closedDays = createTrainerScheduleClosedDays(request, existing);
			existing.changeDefaultLessonTime(
				request.getLessonStartTime(),
				request.getLessonEndTime(),
				request.getLunchStartTime(),
				request.getLunchEndTime(),
				request.getLessonTime(),
				closedDays
			);
		} else {
			TrainerScheduleInfo trainerScheduleInfo = TrainerScheduleInfo.registerDefaultLessonTime(
				request.getLessonStartTime(),
				request.getLessonEndTime(),
				request.getLunchStartTime(),
				request.getLunchEndTime(),
				findTrainer
			);
			List<TrainerScheduleClosedDaysInfo> closedDays = createTrainerScheduleClosedDays(request,
				trainerScheduleInfo);
			trainerScheduleInfo.getTrainerScheduleClosedDays().addAll(closedDays);
			trainerScheduleInfoRepository.save(trainerScheduleInfo);
		}

		return CommandRegisterDefaultLessonTimeResult.from(request);
	}

	private List<TrainerScheduleClosedDaysInfo> createTrainerScheduleClosedDays(
		CommandRegisterDefaultLessonTime request,
		TrainerScheduleInfo trainerScheduleInfo
	) {
		if (request.getClosedDays() != null && request.getClosedDays().size() == 7) {
			throw new IllegalArgumentException("모든 요일이 휴무일이 될 수 없습니다.");
		}

		if (request.getClosedDays() == null) {
			return new ArrayList<>();
		}

		return request.getClosedDays().stream()
			.map(closedDay -> TrainerScheduleClosedDaysInfo.registerClosedDay(closedDay, trainerScheduleInfo))
			.collect(Collectors.toList());
	}

	public CommandRegisterScheduleResult registerSchedule(
		CommandRegisterSchedule request,
		Long trainerId
	) {
		Member trainer = findMemberById(trainerId);

		TrainerScheduleInfo trainerScheduleInfo = trainerScheduleInfoRepository.findOneByTrainerId(trainerId);
		if (trainerScheduleInfo == null) {
			throw new CustomException(ErrorCode.TRAINER_SCHEDULE_NOT_FOUND);
		}

		isScheduleExisting(trainerScheduleInfo, request, trainerId);

		LocalDate lessonDt = request.getLessonStartDt();
		LocalDate lessonEndDt = request.getLessonEndDt();
		long lessonTime = trainerScheduleInfo.getLessonTime().getDescription().longValue();

		List<Schedule> schedules = new ArrayList<>();

		while (!lessonDt.isAfter(lessonEndDt)) {
			LocalDateTime defaultLessonStartTime = LocalDateTime.of(lessonDt, trainerScheduleInfo.getLessonStartTime());
			LocalDateTime defaultLessonEndTime = LocalDateTime.of(lessonDt, trainerScheduleInfo.getLessonEndTime());
			LocalDateTime dayLessonStartTime = LocalDateTime.of(lessonDt, LocalTime.of(6, 0));
			LocalDateTime dayLessonEndTime = LocalDateTime.of(lessonDt.plusDays(1), LocalTime.of(0, 0));

			if (isClosedDay(trainerScheduleInfo, lessonDt)) {
				generateDisabledSchedules(
					schedules, lessonDt, trainer,
					dayLessonStartTime, dayLessonEndTime, lessonTime
				);
				lessonDt = lessonDt.plusDays(ONE_DAY);
				continue;
			}

			generateAvailableSchedules(
				schedules, lessonDt, trainer,
				defaultLessonStartTime, defaultLessonEndTime,
				trainerScheduleInfo, lessonTime
			);

			lessonDt = lessonDt.plusDays(ONE_DAY);
		}

		trainerScheduleRepository.saveAll(schedules);

		return CommandRegisterScheduleResult.from(schedules, trainerScheduleInfo);
	}

	private boolean isStartTimeEqualsLunchStartTime(LocalTime lunchStartTime, LocalTime startTime) {
		return startTime.equals(lunchStartTime);
	}

	private boolean isClosedDay(TrainerScheduleInfo trainerScheduleInfo, LocalDate lessonDt) {
		return trainerScheduleInfo.getTrainerScheduleClosedDays().stream()
			.anyMatch(day -> day.getClosedDays() == lessonDt.getDayOfWeek());
	}

	public List<CommandScheduleStatusResult> updateScheduleStatus(
		CommandUpdateScheduleStatus request,
		ReservationStatus status,
		Long memberId
	) {
		List<Schedule> schedules;

		switch (status) {
			case AVAILABLE:
				schedules = trainerScheduleRepository.findAllSchedule(
					request.getScheduleIds(),
					List.of(ReservationStatus.DISABLED),
					memberId
				);
				if (schedules.isEmpty()) {
					throw new CustomException(ErrorCode.SCHEDULE_NOT_FOUND);
				}
				schedules.forEach(Schedule::updateLessonDtToAvailableDay);
				break;

			case DISABLED:
				schedules = trainerScheduleRepository.findAllSchedule(
					request.getScheduleIds(),
					List.of(ReservationStatus.AVAILABLE, ReservationStatus.COMPLETED),
					memberId
				);
				if (schedules.isEmpty()) {
					throw new CustomException(ErrorCode.SCHEDULE_NOT_FOUND);
				}
				schedules.forEach(s -> {
					s.updateScheduleToDisabled();
					if (s.getScheduleWaiting() != null && !s.getScheduleWaiting().isEmpty()) {
						scheduleWaitingRepository.deleteAll(s.getScheduleWaiting());
					}
				});
				break;

			default:
				throw new CustomException(ErrorCode.RESERVATION_STATUS_NOT_FOUND);
		}

		return schedules.stream()
			.map(CommandScheduleStatusResult::from)
			.collect(Collectors.toList());
	}

	public CommandRegisterScheduleByStudentResult registerStudentInTrainerSchedule(
		Long scheduleId,
		Long studentId,
		Long trainerId
	) {
		Schedule schedule = trainerScheduleRepository.findAllSchedule(scheduleId, ReservationStatus.AVAILABLE,
			trainerId);
		if (schedule == null) {
			throw new CustomException(ErrorCode.SCHEDULE_NOT_FOUND);
		}

		Member findStudent = memberRepository.findById(studentId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		schedule.registerSchedule(findStudent);

		CommandSendNotification notification = CommandSendNotification.builder()
			.title(NotificationType.RESERVE.getDescription())
			.content(String.format(NotificationType.RESERVE.getContent(),
				schedule.getTrainer().getName(),
				schedule.getApplicant().getName(),
				LocalDateTime.of(schedule.getLessonDt(), schedule.getLessonStartTime())
					.format(LessonTimeFormatter.lessonStartDateTimeFormatter())))
			.receiverIds(List.of(schedule.getApplicant().getId()))
			.notificationType(NotificationType.RESERVE)
			.notificationCategory(NotificationCategory.SCHEDULE)
			.clickUrl("https://main.to-be-healthy.shop/student/schedule?tab=myReservation")
			.studentId(schedule.getApplicant().getId())
			.studentName(schedule.getApplicant().getName())
			.build();

		notificationPublisher.publish(notification, EventType.NOTIFICATION);

		return CommandRegisterScheduleByStudentResult.from(schedule, findStudent);
	}

	public CommandCancelStudentReservationResult cancelStudentReservation(
		Long scheduleId,
		Long trainerId
	) {
		Schedule schedule = trainerScheduleRepository.findAllSchedule(scheduleId, ReservationStatus.COMPLETED,
			trainerId);
		if (schedule == null) {
			throw new CustomException(ErrorCode.SCHEDULE_NOT_FOUND);
		}

		Long applicantId = schedule.getApplicant() != null ? schedule.getApplicant().getId() : null;
		String applicantName = schedule.getApplicant() != null ? schedule.getApplicant().getName() : null;

		schedule.cancelMemberSchedule();

		CommandSendNotification notification = CommandSendNotification.builder()
			.title(NotificationType.CANCEL.getDescription())
			.content(String.format(NotificationType.CANCEL.getContent(),
				schedule.getTrainer().getName(),
				applicantName,
				LocalDateTime.of(schedule.getLessonDt(), schedule.getLessonStartTime())
					.format(LessonTimeFormatter.lessonStartDateTimeFormatter())))
			.receiverIds(List.of(applicantId))
			.notificationType(NotificationType.CANCEL)
			.notificationCategory(NotificationCategory.SCHEDULE)
			.clickUrl("https://main.to-be-healthy.shop/student/schedule?tab=myReservation")
			.build();

		notificationPublisher.publish(notification, EventType.NOTIFICATION);

		eventPublisher.publish(schedule.getId(), EventType.SCHEDULE_CANCEL);

		return CommandCancelStudentReservationResult.from(schedule, applicantId, applicantName);
	}

	public ScheduleIdInfo updateReservationStatusToNoShow(Long scheduleId, Long trainerId) {
		Schedule schedule = trainerScheduleRepository.findAllSchedule(scheduleId, ReservationStatus.COMPLETED,
			trainerId);
		if (schedule == null) {
			throw new CustomException(ErrorCode.SCHEDULE_NOT_FOUND);
		}
		schedule.updateReservationStatusToNoShow(ReservationStatus.NO_SHOW);
		return ScheduleIdInfo.from(schedule);
	}

	public ScheduleIdInfo cancelReservationStatusToNoShow(Long scheduleId, Long trainerId) {
		Schedule schedule = trainerScheduleRepository.findAllSchedule(scheduleId, ReservationStatus.NO_SHOW, trainerId);
		if (schedule == null) {
			throw new CustomException(ErrorCode.SCHEDULE_NOT_FOUND);
		}
		schedule.updateReservationStatusToNoShow(ReservationStatus.COMPLETED);
		return ScheduleIdInfo.from(schedule);
	}

	private void isScheduleExisting(
		TrainerScheduleInfo trainerScheduleInfo,
		CommandRegisterSchedule request,
		Long trainerId
	) {
		boolean isDuplicateSchedule = trainerScheduleRepository.validateDuplicateSchedule(
			trainerScheduleInfo, request, trainerId
		);
		if (isDuplicateSchedule) {
			throw new CustomException(ErrorCode.SCHEDULE_ALREADY_EXISTS);
		}
	}

	private Member findMemberById(Long trainerId) {
		return memberRepository.findById(trainerId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
	}

	public void deleteDisabledSchedule() {
		LocalDate today = LocalDate.now();
		LocalDate startOfLastWeek = today.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
		LocalDate endOfLastWeek = startOfLastWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
		List<Schedule> disabledSchedule = trainerScheduleRepository.findAllDisabledSchedule(startOfLastWeek,
			endOfLastWeek);
		trainerScheduleRepository.deleteAll(disabledSchedule);
	}

	private void generateDisabledSchedules(
		List<Schedule> schedules,
		LocalDate lessonDt,
		Member trainer,
		LocalDateTime startTime,
		LocalDateTime endTime,
		long lessonTime
	) {
		LocalDateTime currentTime = startTime;
		while (currentTime.isBefore(endTime)) {
			Schedule schedule = Schedule.registerSchedule(
				lessonDt, trainer,
				currentTime.toLocalTime(),
				currentTime.plusMinutes(lessonTime).toLocalTime(),
				ReservationStatus.DISABLED
			);
			schedules.add(schedule);
			currentTime = currentTime.plusMinutes(lessonTime);
		}
	}

	private void generateAvailableSchedules(
		List<Schedule> schedules,
		LocalDate lessonDt,
		Member trainer,
		LocalDateTime startTime,
		LocalDateTime endTime,
		TrainerScheduleInfo trainerScheduleInfo,
		long lessonTime
	) {
		LocalDateTime currentTime = startTime;
		while (currentTime.isBefore(endTime)) {
			if (isStartTimeEqualsLunchStartTime(trainerScheduleInfo.getLunchStartTime(), currentTime.toLocalTime())) {
				Duration duration = Duration.between(trainerScheduleInfo.getLunchStartTime(),
					trainerScheduleInfo.getLunchEndTime());
				schedules.add(
					Schedule.registerSchedule(
						lessonDt, trainer,
						trainerScheduleInfo.getLunchStartTime(),
						trainerScheduleInfo.getLunchEndTime(),
						ReservationStatus.DISABLED
					)
				);
				currentTime = currentTime.plusMinutes(duration.toMinutes());
			} else {
				schedules.add(
					Schedule.registerSchedule(
						lessonDt, trainer,
						currentTime.toLocalTime(),
						currentTime.plusMinutes(lessonTime).toLocalTime(),
						ReservationStatus.AVAILABLE
					)
				);
				currentTime = currentTime.plusMinutes(lessonTime);
			}
		}
	}
}
