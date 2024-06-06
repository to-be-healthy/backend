package com.tobe.healthy.member.domain.dto.out;

import com.tobe.healthy.gym.domain.dto.out.GymDto;
import com.tobe.healthy.member.domain.dto.ProfileDto;
import com.tobe.healthy.member.domain.entity.AlarmStatus;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberType;
import com.tobe.healthy.member.domain.entity.SocialType;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
@Builder
public class MemberInfoResult {

	private Long id;
	private String userId;
	private String email;
	private String name;

	private ProfileDto profile;
	private GymDto gym;
	private MemberType memberType;
	private AlarmStatus pushAlarmStatus;
	private AlarmStatus communityAlarmStatus;
	private AlarmStatus feedbackAlarmStatus;
	private AlarmStatus scheduleNoticeStatus;
	private SocialType socialType;


	public static MemberInfoResult create(Member member) {
		MemberInfoResultBuilder builder = MemberInfoResult.builder()
				.id(member.getId())
				.userId(member.getUserId())
				.email(member.getEmail())
				.name(member.getName())
				.memberType(member.getMemberType())
				.pushAlarmStatus(member.getPushAlarmStatus())
				.communityAlarmStatus(member.getCommunityAlarmStatus())
				.feedbackAlarmStatus(member.getFeedbackAlarmStatus())
				.scheduleNoticeStatus(member.getScheduleNoticeStatus())
				.socialType(member.getSocialType());

		if(member.getMemberProfile() != null){
			builder.profile(ProfileDto.from(member.getMemberProfile()));
		}
		if(member.getGym() != null){
			builder.gym(GymDto.from(member.getGym()));
		}
		return builder.build();
	}
}
