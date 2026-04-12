package com.tobe.healthy.member.presentation.dto.out;

import com.tobe.healthy.member.presentation.dto.MemberDto;
import com.tobe.healthy.member.domain.Member;

public record InvitationMappingResult(
	MemberDto trainer,
	String name,
	int lessonCnt
) {

	public static InvitationMappingResult create(Member member, String name, int lessonCnt) {
		return new InvitationMappingResult(
			MemberDto.from(member),
			name,
			lessonCnt
		);
	}
}
