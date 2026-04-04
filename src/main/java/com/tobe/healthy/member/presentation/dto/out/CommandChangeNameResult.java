package com.tobe.healthy.member.presentation.dto.out;

import com.tobe.healthy.member.domain.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
public class CommandChangeNameResult {
	private Long memberId;
	private String name;

	public static CommandChangeNameResult from(Member member) {
		return CommandChangeNameResult.builder()
			.memberId(member.getId())
			.name(member.getName())
			.build();
	}
}
