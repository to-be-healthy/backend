package com.tobe.healthy.workout.presentation.dto;

import com.tobe.healthy.member.domain.Member;
import com.tobe.healthy.member.domain.MemberProfile;

public record CommentMemberDto(
	Long memberId,
	String name,
	String fileUrl
) {

	public static CommentMemberDto from(Member member) {
		return new CommentMemberDto(
			member.getId(),
			member.getName(),
			null
		);
	}

	public static CommentMemberDto create(Member member, MemberProfile memberProfile) {
		String fileUrl = memberProfile != null ? memberProfile.getFileUrl() : null;
		return new CommentMemberDto(
			member.getId(),
			member.getName(),
			fileUrl
		);
	}
}
