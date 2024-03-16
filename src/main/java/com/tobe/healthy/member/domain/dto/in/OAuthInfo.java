package com.tobe.healthy.member.domain.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OAuthInfo {

	@JsonProperty("access_token")
	private String accessToken;

	@JsonProperty("token_type")
	private String tokenType;

	@JsonProperty("refresh_token")
	private String refreshToken;

	@JsonProperty("expires_in")
	private int expiresIn;

	private String scope;

	@JsonProperty("refresh_token_expires_in")
	private int refreshTokenExpiresIn;

	@Data
	public static class NaverUserInfo {
		@JsonProperty("resultcode")
		private String resultCode;

		private String message;

		private NaverUserInfoDetail response;

		@Data
		public static class NaverUserInfoDetail {
			private String id;

			@JsonProperty("profile_image")
			private String profileImage;

			private String email;

			private String name;
		}
	}

	// 카카오 시작
	@Data
	public static class KakaoUserInfo {

		private Long id;

		@JsonProperty("connected_at")
		private String connectedAt;

		private Properties properties;

		@JsonProperty("kakao_account")
		private KakaoAccount kakaoAccount;

		@Data
		public static class Properties {

			private String nickname;

			@JsonProperty("profile_image")
			private String profileImage;

			@JsonProperty("thumbnail_image")
			private String thumbnailImage;
		}

		@Data
		public static class KakaoAccount {

			@JsonProperty("profile_nickname_needs_agreement")
			private boolean profileNicknameNeedsAgreement;

			@JsonProperty("profile_image_needs_agreement")
			private boolean profileImageNeedsAgreement;

			private Profile profile;

			@JsonProperty("has_email")
			private boolean hasEmail;

			@JsonProperty("email_needs_agreement")
			private boolean emailNeedsAgreement;

			@JsonProperty("is_email_valid")
			private boolean isEmailValid;

			@JsonProperty("is_email_verified")
			private boolean isEmailVerified;

			private String email;
		}

		@Data
		public static class Profile {

			private String nickname;

			@JsonProperty("thumbnail_image_url")
			private String thumbnailImageUrl;

			@JsonProperty("profile_image_url")
			private String profileImageUrl;

			@JsonProperty("is_default_image")
			private boolean isDefaultImage;

			@JsonProperty("is_default_nickname")
			private boolean isDefaultNickname;
		}
	}
	// 카카오 끝
}