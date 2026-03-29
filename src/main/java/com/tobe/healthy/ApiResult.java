package com.tobe.healthy;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> {

	@Builder.Default
	private HttpStatus status = HttpStatus.OK;

	private String message;

	private T data;

	public ApiResult(String message, T data) {
		this.status = HttpStatus.OK;
		this.message = message;
		this.data = data;
	}

	public static <T> ApiResult<T> success(T data) {
		return ApiResult.<T>builder()
			.status(HttpStatus.OK)
			.data(data)
			.build();
	}

	public static <T> ApiResult<T> success(String message, T data) {
		return ApiResult.<T>builder()
			.status(HttpStatus.OK)
			.message(message)
			.data(data)
			.build();
	}

	public static ApiResult<Void> success(String message) {
		return ApiResult.<Void>builder()
			.status(HttpStatus.OK)
			.message(message)
			.build();
	}
}
