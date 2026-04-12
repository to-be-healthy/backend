package com.tobe.healthy.member.presentation.dto;

import org.springframework.util.ObjectUtils;

import com.tobe.healthy.member.domain.MemberProfile;

public record ProfileDto(
	Long id,
	String fileUrl
) {

	public static ProfileDto from(MemberProfile memberProfile) {
		if (ObjectUtils.isEmpty(memberProfile)) {
			return null;
		}
		return new ProfileDto(
			memberProfile.getId(),
			memberProfile.getFileUrl()
		);
	}
}
