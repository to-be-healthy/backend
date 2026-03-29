package com.tobe.healthy.notification.presentation;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tobe.healthy.ApiResult;
import com.tobe.healthy.common.KotlinCustomPaging;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.notification.application.NotificationService;
import com.tobe.healthy.notification.presentation.dto.out.CommandNotificationStatusResult;
import com.tobe.healthy.notification.presentation.dto.out.RetrieveNotificationWithRedDotResult.RetrieveNotificationResult;
import com.tobe.healthy.notification.domain.entity.NotificationCategory;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping("/{notificationCategory}")
	public ApiResult<KotlinCustomPaging<RetrieveNotificationResult>> findAllNotification(
		@PathVariable NotificationCategory notificationCategory,
		@AuthenticationPrincipal CustomMemberDetails member,
		@ParameterObject @PageableDefault(size = 10) Pageable pageable) {
		return ApiResult.<KotlinCustomPaging<RetrieveNotificationResult>>builder()
			.message("전체 알림을 조회하였습니다.")
			.data(notificationService.findAllNotification(notificationCategory, member.getMemberId(), pageable))
			.build();
	}

	@PatchMapping("/{notificationId}")
	public ApiResult<CommandNotificationStatusResult> updateNotificationStatus(
		@PathVariable Long notificationId,
		@AuthenticationPrincipal CustomMemberDetails member) {
		return ApiResult.<CommandNotificationStatusResult>builder()
			.message("해당 알림을 읽음 처리 하였습니다.")
			.data(notificationService.updateNotificationStatus(notificationId, member.getMemberId()))
			.build();
	}

	@GetMapping("/red-dot")
	public ApiResult<Boolean> findNotificationWithRedDot(
		@AuthenticationPrincipal CustomMemberDetails member) {
		return ApiResult.<Boolean>builder()
			.message("red-dot 상태를 조회하였습니다.")
			.data(notificationService.findRedDotStatus(member.getMemberId()))
			.build();
	}
}
