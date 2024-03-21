package com.tobe.healthy.member.domain.entity;

import com.tobe.healthy.common.EnumMapperType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SocialType implements EnumMapperType {
	NONE("없음"),
	KAKAO("카카오"),
	NAVER("네이버"),
	GOOGLE("구글");

	private final String description;

	@Override
	public String getCode() {
		return name();
	}
}