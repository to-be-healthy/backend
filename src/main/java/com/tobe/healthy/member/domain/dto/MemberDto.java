package com.tobe.healthy.member.domain.dto;

import com.tobe.healthy.gym.domain.dto.out.GymDto;
import com.tobe.healthy.gym.domain.entity.Gym;
import com.tobe.healthy.member.domain.entity.AlarmStatus;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberProfile;
import com.tobe.healthy.member.domain.entity.MemberType;
import com.tobe.healthy.member.domain.entity.SocialType;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.util.ObjectUtils;


@Data
@ToString
@Builder
public class MemberDto {

	private Long id;
	private String userId;
	private String email;
	private String name;
	private int age;
	private int height;
	private int weight;
	private boolean delYn;

	private ProfileDto profile;
	private MemberType memberType;
	private AlarmStatus pushAlarmStatus;
	private AlarmStatus feedbackAlarmStatus;
	private GymDto gym;
	private SocialType socialType;

	public static MemberDto from(Member member) {
		MemberDtoBuilder builder = MemberDto.builder()
			.id(member.getId())
			.userId(member.getUserId())
			.email(member.getEmail())
			.name(member.getName())
			.delYn(member.isDelYn())
			.memberType(member.getMemberType())
			.pushAlarmStatus(member.getPushAlarmStatus())
			.feedbackAlarmStatus(member.getFeedbackAlarmStatus())
			.socialType(member.getSocialType());
		return builder.build();
	}

	public static MemberDto create(Member member, MemberProfile memberProfile) {
		MemberDtoBuilder builder = MemberDto.builder()
				.id(member.getId())
				.userId(member.getUserId())
				.email(member.getEmail())
				.name(member.getName())
				.delYn(member.isDelYn())
				.memberType(member.getMemberType())
				.pushAlarmStatus(member.getPushAlarmStatus())
				.feedbackAlarmStatus(member.getFeedbackAlarmStatus())
				.socialType(member.getSocialType());

		if(memberProfile != null){
			builder.profile(ProfileDto.from(memberProfile));
		}
		return builder.build();
	}

	public static MemberDto create(Member member, MemberProfile memberProfile, Gym gym) {
		MemberDtoBuilder builder = MemberDto.builder()
				.id(member.getId())
				.userId(member.getUserId())
				.email(member.getEmail())
				.name(member.getName())
				.delYn(member.isDelYn())
				.memberType(member.getMemberType())
				.pushAlarmStatus(member.getPushAlarmStatus())
				.feedbackAlarmStatus(member.getFeedbackAlarmStatus())
				.socialType(member.getSocialType());

		if(memberProfile != null){
			builder.profile(ProfileDto.from(memberProfile));
		}
		if(member.getGym() != null){
			builder.gym(GymDto.from(gym));
		}
		return builder.build();
	}

}
