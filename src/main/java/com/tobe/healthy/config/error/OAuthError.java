package com.tobe.healthy.config.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OAuthError {

	@Data
	public static class KakaoError {
		@JsonProperty("error")
		private String error;

		@JsonProperty("error_description")
		private String errorDescription;

		@JsonProperty("error_code")
		private String errorCode;
	}

	@Data
	public static class NaverError {
		private String resultcode;
		private String message;
	}
}
