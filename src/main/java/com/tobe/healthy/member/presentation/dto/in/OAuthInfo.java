package com.tobe.healthy.member.presentation.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OAuthInfo(
	@JsonProperty("access_token")
	String accessToken,

	@JsonProperty("token_type")
	String tokenType,

	@JsonProperty("refresh_token")
	String refreshToken,

	@JsonProperty("id_token")
	String idToken,

	@JsonProperty("expires_in")
	int expiresIn,

	String scope,

	@JsonProperty("refresh_token_expires_in")
	int refreshTokenExpiresIn
) {

	public record NaverUserInfo(
		@JsonProperty("resultcode")
		String resultCode,

		String message,

		NaverUserInfoDetail response
	) {

		public record NaverUserInfoDetail(
			String id,

			@JsonProperty("profile_image")
			String profileImage,

			String email,
			String name
		) {}
	}

	public record KakaoUserInfo(
		Long id,

		@JsonProperty("connected_at")
		String connectedAt,

		Properties properties,

		@JsonProperty("kakao_account")
		KakaoAccount kakaoAccount
	) {

		public record Properties(
			String nickname,

			@JsonProperty("profile_image")
			String profileImage,

			@JsonProperty("thumbnail_image")
			String thumbnailImage
		) {}

		public record KakaoAccount(
			@JsonProperty("profile_nickname_needs_agreement")
			boolean profileNicknameNeedsAgreement,

			@JsonProperty("profile_image_needs_agreement")
			boolean profileImageNeedsAgreement,

			Profile profile,

			@JsonProperty("has_email")
			boolean hasEmail,

			@JsonProperty("email_needs_agreement")
			boolean emailNeedsAgreement,

			@JsonProperty("is_email_valid")
			boolean isEmailValid,

			@JsonProperty("is_email_verified")
			boolean isEmailVerified,

			String email
		) {}

		public record Profile(
			String nickname,

			@JsonProperty("thumbnail_image_url")
			String thumbnailImageUrl,

			@JsonProperty("profile_image_url")
			String profileImageUrl,

			@JsonProperty("is_default_image")
			boolean isDefaultImage,

			@JsonProperty("is_default_nickname")
			boolean isDefaultNickname
		) {}
	}

	public record GoogleUserInfo(
		String id,
		String email,
		String name,
		String picture
	) {}
}
