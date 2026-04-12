package com.tobe.healthy.member.presentation.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IdToken(
	Long id,
	String aud,
	String sub,

	@JsonProperty("auth_time")
	int authTime,

	String iss,
	String nickname,
	int exp,
	int iat,
	String picture,
	String email,
	String name,

	@JsonProperty("c_hash")
	String cHash,

	@JsonProperty("email_verified")
	boolean emailVerified,

	@JsonProperty("nonce_supported")
	boolean nonceSupported,

	@JsonProperty("is_private_email")
	boolean isPrivateEmail
) {

	public IdToken withId(Long newId) {
		return new IdToken(newId, aud, sub, authTime, iss, nickname, exp, iat, picture, email, name, cHash,
			emailVerified, nonceSupported, isPrivateEmail);
	}
}
