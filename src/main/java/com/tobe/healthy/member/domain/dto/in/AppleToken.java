package com.tobe.healthy.member.domain.dto.in;

import lombok.Data;

@Data
public class AppleToken {
	private String access_token;
	private String token_type;
	private int expires_in;
	private String refresh_token;
}
