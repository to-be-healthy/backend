package com.tobe.healthy.member.domain.dto.out;

import com.tobe.healthy.file.domain.dto.ProfileDto;
import com.tobe.healthy.file.domain.entity.Profile;
import com.tobe.healthy.gym.domain.dto.GymDto;
import com.tobe.healthy.gym.domain.entity.Gym;
import com.tobe.healthy.member.domain.dto.MemberDto;
import com.tobe.healthy.member.domain.entity.AlarmStatus;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberType;
import com.tobe.healthy.member.domain.entity.SocialType;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class MemberInfoResult {

	private Long id;
	private String userId;
	private String email;
	private String name;
	private int age;
	private int height;
	private int weight;

	private ProfileDto profile;
	private GymDto gym;
	private MemberType memberType;
	private AlarmStatus pushAlarmStatus;
	private AlarmStatus feedbackAlarmStatus;
	private SocialType socialType;


	public static MemberInfoResult create(Member member) {
		MemberInfoResultBuilder builder = MemberInfoResult.builder()
				.id(member.getId())
				.userId(member.getUserId())
				.email(member.getEmail())
				.name(member.getName())
				.age(member.getAge())
				.height(member.getHeight())
				.weight(member.getWeight())
				.memberType(member.getMemberType())
				.pushAlarmStatus(member.getPushAlarmStatus())
				.feedbackAlarmStatus(member.getFeedbackAlarmStatus())
				.socialType(member.getSocialType());

		if(member.getProfileId() != null){
			builder.profile(ProfileDto.from(member.getProfileId()));
		}
		if(member.getGym() != null){
			builder.gym(GymDto.from(member.getGym()));
		}
		return builder.build();
	}
}