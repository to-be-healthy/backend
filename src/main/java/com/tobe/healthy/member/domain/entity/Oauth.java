package com.tobe.healthy.member.domain.entity;

import com.tobe.healthy.common.EnumMapperType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Oauth implements EnumMapperType {
	GRANT_TYPE("authorization_code"),
	CLIENT_ID("b744b34e90d30c3a0ff41ad4ade070f7"),
	REDIRECT_URL("http://localhost:8080/api/v1/auth/code/kakao"),
	CLIENT_SECRET("QMaOCZDGKnrCtnRbSl3nIRmsKVIPGJnd"),
	KAKAO_TOKEN_URL("https://kauth.kakao.com/oauth/token");

	private final String description;

	@Override
	public String getCode() {
		return name();
	}
}
