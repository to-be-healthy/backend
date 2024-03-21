package com.tobe.healthy.member.domain.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class IdToken {
	private String kid;
	private String typ;
	private String alg;
	private String aud;
	private String sub;
	@JsonProperty("auth_time")
	private long authTime;
	private String iss;
	private String nickname;
	private long exp;
	private long iat;
	private String picture;
	private String email;
}
