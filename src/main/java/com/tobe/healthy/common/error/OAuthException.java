package com.tobe.healthy.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OAuthException extends RuntimeException {
	private String message;
}
