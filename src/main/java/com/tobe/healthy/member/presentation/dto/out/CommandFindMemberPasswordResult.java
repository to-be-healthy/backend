package com.tobe.healthy.member.presentation.dto.out;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tobe.healthy.member.domain.Member;
import com.tobe.healthy.member.domain.SocialType;

public record CommandFindMemberPasswordResult(
	@JsonIgnore String email,
	@JsonIgnore String message,
	SocialType socialType
) {

	public static CommandFindMemberPasswordResult from(Member member, String message) {
		return new CommandFindMemberPasswordResult(
			member.getEmail(),
			message,
			member.getSocialType()
		);
	}
}
