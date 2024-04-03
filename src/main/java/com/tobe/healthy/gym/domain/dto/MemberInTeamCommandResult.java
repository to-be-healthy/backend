package com.tobe.healthy.gym.domain.dto;

import com.tobe.healthy.member.domain.entity.Member;
import lombok.Data;

@Data
public class MemberInTeamCommandResult {
	private String name;
	private String userId;
	private String email;

	public MemberInTeamCommandResult(Member member) {
		this.name = member.getName();
		this.userId = member.getUserId();
		this.email = member.getEmail();
	}
}
