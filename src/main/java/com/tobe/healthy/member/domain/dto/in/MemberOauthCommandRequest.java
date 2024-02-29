package com.tobe.healthy.member.domain.dto.in;

import lombok.Data;

@Data
public class MemberOauthCommandRequest {
	private String code;
	private String error;
	private String errorDescription;
	private String state;
}
