package com.tobe.healthy.config.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OAuthException extends RuntimeException {
	private String message;
}
