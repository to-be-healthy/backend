package com.tobe.healthy.member.domain.entity;

import com.tobe.healthy.common.EnumMapperType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OAuth implements EnumMapperType {
	KAKAO_GRANT_TYPE("authorization_code"),
	KAKAO_CLIENT_ID("b744b34e90d30c3a0ff41ad4ade070f7"),
	KAKAO_REDIRECT_URL("https://api.to-be-healthy.site/api/v1/auth/kakao"),
	KAKAO_CLIENT_SECRET("QMaOCZDGKnrCtnRbSl3nIRmsKVIPGJnd"),
	KAKAO_TOKEN_URL("https://kauth.kakao.com/oauth/token");

	private final String description;

	@Override
	public String getCode() {
		return name();
	}
}
