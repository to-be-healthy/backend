package com.tobe.healthy.gym.domain.dto.out;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberProfile;
import lombok.Data;

@Data
public class TrainerCommandResult {
	private Long id;
	private String userId;
	private String email;
	private String name;
	private MemberProfile memberProfile;

	public TrainerCommandResult(Member member) {
		this.id = member.getId();
		this.userId = member.getUserId();
		this.email = member.getEmail();
		this.name = member.getName();
	}
}
