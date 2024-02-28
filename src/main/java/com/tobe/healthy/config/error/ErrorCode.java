package com.tobe.healthy.config.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	MEMBER_NOT_FOUND(400, "C_000", "회원이 존재하지 않습니다."),
	MEMBER_DUPLICATION_EMAIL(401, "C_001", "중복된 이메일이 존재합니다."),
	MEMBER_DUPLICATION_NICKNAME(405, "C_005", "중복된 닉네임이 존재합니다."),
	ACCESS_TOKEN_EXPIRED(402, "C_002", "토큰이 만료되었습니다."),
	HANDLE_ACCESS_DENIED(403, "C_003", "권한이 없습니다."),
	ACCESS_TOKEN_NOT_FOUND(404, "C_004", "토큰을 찾을 수 없습니다."),
	REFRESH_TOKEN_EXPIRED(405, "C005", "갱신 토큰이 만료되었습니다."),
	REFRESH_TOKEN_NOT_FOUND(406, "C006", "갱신 토큰을 찾을 수 없습니다."),
	SERVER_ERROR(500, "S_001", "서버에서 오류가 발생하였습니다."),
	FILE_FIND_ERROR(503, "S_003", "파일 조회중 에러가 발생하였습니다."),
	FILE_UPLOAD_ERROR(501, "S_002", "파일 업로드중 에러가 발생하였습니다.");

	private final int status;
	private final String code;
	private final String message;
}
