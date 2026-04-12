package com.tobe.healthy.member.presentation.dto;

import com.tobe.healthy.gym.presentation.dto.out.GymDto;
import com.tobe.healthy.gym.domain.Gym;
import com.tobe.healthy.member.domain.AlarmStatus;
import com.tobe.healthy.member.domain.Member;
import com.tobe.healthy.member.domain.MemberProfile;
import com.tobe.healthy.member.domain.MemberType;
import com.tobe.healthy.member.domain.SocialType;

public record MemberDto(
	Long id,
	String userId,
	String email,
	String name,
	boolean delYn,
	ProfileDto profile,
	MemberType memberType,
	AlarmStatus pushAlarmStatus,
	AlarmStatus feedbackAlarmStatus,
	GymDto gym,
	SocialType socialType
) {

	public static MemberDto from(Member member) {
		return new MemberDto(
			member.getId(),
			member.getUserId(),
			member.getEmail(),
			member.getName(),
			member.isDelYn(),
			null,
			member.getMemberType(),
			member.getPushAlarmStatus(),
			member.getFeedbackAlarmStatus(),
			null,
			member.getSocialType()
		);
	}

	public static MemberDto create(Member member, MemberProfile memberProfile) {
		ProfileDto profileDto = memberProfile != null ? ProfileDto.from(memberProfile) : null;
		return new MemberDto(
			member.getId(),
			member.getUserId(),
			member.getEmail(),
			member.getName(),
			member.isDelYn(),
			profileDto,
			member.getMemberType(),
			member.getPushAlarmStatus(),
			member.getFeedbackAlarmStatus(),
			null,
			member.getSocialType()
		);
	}

	public static MemberDto create(Member member, MemberProfile memberProfile, Gym gym) {
		ProfileDto profileDto = memberProfile != null ? ProfileDto.from(memberProfile) : null;
		GymDto gymDto = member.getGym() != null ? GymDto.from(gym) : null;
		return new MemberDto(
			member.getId(),
			member.getUserId(),
			member.getEmail(),
			member.getName(),
			member.isDelYn(),
			profileDto,
			member.getMemberType(),
			member.getPushAlarmStatus(),
			member.getFeedbackAlarmStatus(),
			gymDto,
			member.getSocialType()
		);
	}

}
