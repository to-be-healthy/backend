package com.tobe.healthy.member.presentation.dto.out;

import com.tobe.healthy.member.domain.Member;

public record CommandChangeNameResult(
	Long memberId,
	String name
) {

	public static CommandChangeNameResult from(Member member) {
		return new CommandChangeNameResult(
			member.getId(),
			member.getName()
		);
	}
}
