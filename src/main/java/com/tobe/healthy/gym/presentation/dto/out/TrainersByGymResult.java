package com.tobe.healthy.gym.presentation.dto.out;

import com.tobe.healthy.member.domain.Member;
import com.tobe.healthy.member.domain.MemberProfile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainersByGymResult {
	private Long id;
	private String userId;
	private String email;
	private String name;
	private MemberProfileResult profile;

	public static TrainersByGymResult from(Member member) {
		return TrainersByGymResult.builder()
			.id(member.getId())
			.userId(member.getUserId())
			.email(member.getEmail())
			.name(member.getName())
			.profile(MemberProfileResult.from(member.getMemberProfile()))
			.build();
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MemberProfileResult {
		private Long id;
		private String fileUrl;

		public static MemberProfileResult from(MemberProfile memberProfile) {
			if (memberProfile == null) {
				return MemberProfileResult.builder().build();
			}
			return MemberProfileResult.builder()
				.id(memberProfile.getId())
				.fileUrl(memberProfile.getFileUrl())
				.build();
		}
	}
}
