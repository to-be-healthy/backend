package com.tobe.healthy.member.presentation.dto.out;

import com.tobe.healthy.gym.presentation.dto.out.GymDto;
import com.tobe.healthy.member.presentation.dto.ProfileDto;
import com.tobe.healthy.member.domain.AlarmStatus;
import com.tobe.healthy.member.domain.Member;
import com.tobe.healthy.member.domain.MemberType;
import com.tobe.healthy.member.domain.SocialType;

public record MemberInfoResult(
	Long id,
	String userId,
	String email,
	String name,
	ProfileDto profile,
	GymDto gym,
	MemberType memberType,
	AlarmStatus pushAlarmStatus,
	AlarmStatus communityAlarmStatus,
	AlarmStatus feedbackAlarmStatus,
	AlarmStatus scheduleNoticeStatus,
	SocialType socialType
) {

	public static MemberInfoResult create(Member member) {
		ProfileDto profileDto = member.getMemberProfile() != null
			? ProfileDto.from(member.getMemberProfile()) : null;
		GymDto gymDto = member.getGym() != null
			? GymDto.from(member.getGym()) : null;
		return new MemberInfoResult(
			member.getId(),
			member.getUserId(),
			member.getEmail(),
			member.getName(),
			profileDto,
			gymDto,
			member.getMemberType(),
			member.getPushAlarmStatus(),
			member.getCommunityAlarmStatus(),
			member.getFeedbackAlarmStatus(),
			member.getScheduleNoticeStatus(),
			member.getSocialType()
		);
	}
}
