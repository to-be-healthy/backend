package com.tobe.healthy;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tobe.healthy.common.KotlinCustomPaging;
import com.tobe.healthy.common.error.CustomException;
import com.tobe.healthy.notification.presentation.dto.out.CommandNotificationStatusResult;
import com.tobe.healthy.notification.presentation.dto.out.RetrieveNotificationWithRedDotResult;
import com.tobe.healthy.schedule.presentation.dto.in.CommandRegisterSchedule;
import com.tobe.healthy.schedule.presentation.dto.in.RetrieveTrainerScheduleByLessonInfo;

class ApiContractSerializationTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void apiResultResponseDefaultsStatusToOk() {
		ApiResult<String> response = new ApiResult<>("ok", "payload");

		assertEquals(org.springframework.http.HttpStatus.OK, response.getStatus());
		assertEquals("ok", response.getMessage());
		assertEquals("payload", response.getData());
	}

	@Test
	void kotlinCustomPagingKeepsIsLastPropertyName() throws Exception {
		KotlinCustomPaging<String> paging = new KotlinCustomPaging<>(List.of("item"), 0, 10, 1, 1L, true);

		JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(paging));

		assertTrue(json.has("isLast"));
		assertEquals(true, json.get("isLast").booleanValue());
		assertFalse(json.has("last"));
	}

	@Test
	void notificationDtosKeepIsReadPropertyName() throws Exception {
		CommandNotificationStatusResult statusResult = CommandNotificationStatusResult.builder()
			.notificationId(1L)
			.isRead(true)
			.build();

		RetrieveNotificationWithRedDotResult.RetrieveNotificationResult notificationResult =
			RetrieveNotificationWithRedDotResult.RetrieveNotificationResult.builder()
				.notificationId(2L)
				.notificationCategoryAndType("SCHEDULE-FEEDBACK")
				.title("title")
				.content("content")
				.createdAt("2026-03-28T00:00:00")
				.isRead(true)
				.build();

		JsonNode statusJson = objectMapper.readTree(objectMapper.writeValueAsString(statusResult));
		JsonNode notificationJson = objectMapper.readTree(objectMapper.writeValueAsString(notificationResult));

		assertTrue(statusJson.has("isRead"));
		assertEquals(true, statusJson.get("isRead").booleanValue());
		assertFalse(statusJson.has("read"));

		assertTrue(notificationJson.has("isRead"));
		assertEquals(true, notificationJson.get("isRead").booleanValue());
		assertFalse(notificationJson.has("read"));
	}

	@Test
	void scheduleRequestKeepsConstructorValidation() {
		CustomException exception = assertThrows(
			CustomException.class,
			() -> new CommandRegisterSchedule(
				LocalDate.of(2026, 3, 30),
				LocalDate.of(2026, 3, 29)
			)
		);

		assertEquals("수업 시작일은 종료일보다 빨라야 합니다.", exception.getMessage());
	}

	@Test
	void scheduleSearchRequestKeepsDefaultMonthWhenEmpty() {
		RetrieveTrainerScheduleByLessonInfo request = new RetrieveTrainerScheduleByLessonInfo();

		assertEquals(LocalDate.now().withDayOfMonth(1).toString().substring(0, 7), request.getLessonDt());
		assertNull(request.getLessonStartDt());
		assertNull(request.getLessonEndDt());
	}
}
