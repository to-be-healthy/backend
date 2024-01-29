package com.tobe.healthy.config.error;

import lombok.Getter;

@Getter
public enum ErrorCode {
	MEMBER_NOT_FOUND(400, "C_000", "이메일 또는 비밀번호를 잘못 입력하셨습니다."),
	MEMBER_DUPLICATION(401, "C_001", "중복된 이메일이 존재합니다."),
	ACCESS_TOKEN_EXPIRED(402, "C_002", "토큰이 만료되었습니다."),
	HANDLE_ACCESS_DENIED(403, "C_003", "권한이 없습니다."),
	SERVER_ERROR(500, "S_001", "서버에서 오류가 발생하였습니다.");

	private final String code;
	private final String message;
	private final int status;

	ErrorCode(int status, String code, String message) {
		this.status = status;
		this.message = message;
		this.code = code;
	}
}
