package com.tobe.healthy.member.domain.entity;

import com.tobe.healthy.gym.domain.entity.Gym;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Tokens {
    private String accessToken;
    private String refreshToken;
    private String userId;
    private MemberType memberType;
	private Long gymId;

	public Tokens(String accessToken, String refreshToken, String userId, MemberType memberType, Gym gym) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.userId = userId;
		this.memberType = memberType;
		this.gymId = gym != null ? gym.getId() : null;
	}
}