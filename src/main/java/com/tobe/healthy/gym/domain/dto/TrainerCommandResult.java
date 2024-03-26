package com.tobe.healthy.gym.domain.dto;

import com.tobe.healthy.file.domain.entity.Profile;
import com.tobe.healthy.member.domain.entity.Member;
import lombok.Data;

@Data
public class TrainerCommandResult {
	private Long id;
	private String userId;
	private String email;
	private String name;
	private Profile profile;

	public TrainerCommandResult(Member member) {
		this.id = member.getId();
		this.userId = member.getUserId();
		this.email = member.getEmail();
		this.name = member.getName();
		this.profile = member.getProfileId();
	}
}
