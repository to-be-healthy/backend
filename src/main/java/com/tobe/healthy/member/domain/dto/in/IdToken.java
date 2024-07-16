package com.tobe.healthy.member.domain.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class IdToken {

	private String aud;

	private String sub;

	@JsonProperty("auth_time")
	private int authTime;

	private String iss;

	private String nickname;

	private int exp;

	private int iat;

	private String picture;

	private String email;

	private String name;

	@JsonProperty("c_hash")
	private String cHash;

	@JsonProperty("email_verified")
	private boolean emailVerified;

	@JsonProperty("nonce_supported")
	private boolean nonceSupported;
}
