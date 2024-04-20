package com.tobe.healthy.config.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	MEMBER_NOT_FOUND(NOT_FOUND, "C_000", "회원이 존재하지 않습니다."),
	MEMBER_EMAIL_DUPLICATION(BAD_REQUEST, "C_001", "이미 가입된 이메일 주소입니다."),
	ACCESS_TOKEN_EXPIRED(BAD_REQUEST, "C_002", "토큰이 만료되었습니다."),
	HANDLE_ACCESS_DENIED(FORBIDDEN, "C_003", "권한이 없습니다."),
	ACCESS_TOKEN_NOT_FOUND(NOT_FOUND, "C_004", "토큰을 찾을 수 없습니다."),
	REFRESH_TOKEN_EXPIRED(BAD_REQUEST, "C_005", "갱신 토큰이 만료되었습니다."),
	REFRESH_TOKEN_NOT_FOUND(NOT_FOUND, "C_006", "갱신 토큰을 찾을 수 없습니다."),
	NOT_RESERVABLE_SCHEDULE(BAD_REQUEST, "C_007", "신청할 수 없는 일정입니다."),
	APPLICATION_FORM_NOT_FOUND(NOT_FOUND, "C_008", "해당 수업이 존재하지 않습니다."),
	WORKOUT_HISTORY_NOT_FOUND(NOT_FOUND, "C_009", "운동기록이 존재하지 않습니다."),
	DIET_NOT_FOUND(NOT_FOUND, "C_009", "식단기록이 존재하지 않습니다."),
	MAIL_AUTH_CODE_NOT_VALID(BAD_REQUEST, "C_010", "인증번호가 일치하지 않습니다.다시 확인해 주세요."),
	REFRESH_TOKEN_NOT_VALID(BAD_REQUEST, "C_011", "갱신 토큰이 유효하지 않습니다."),
	MEMBER_ID_DUPLICATION(BAD_REQUEST, "C_012", "사용할 수 없는 아이디입니다."),
	MEMBER_ALREADY_MAPPED(BAD_REQUEST, "C_013", "이미 등록된 회원입니다."),
	MEMBER_NOT_MAPPED(BAD_REQUEST, "C_013", "내 학생이 아닙니다."),
	NOT_STAND_BY_SCHEDULE(BAD_REQUEST, "C_014", "대기할 수 없는 일정입니다."),
	SCHEDULE_NOT_FOUND(NOT_FOUND, "C_015", "일정을 찾을 수 없습니다."),
	WORKOUT_HISTORY_COMMENT_NOT_FOUND(NOT_FOUND, "C_016", "댓글이 존재하지 않습니다."),
	STAND_BY_SCHEDULE_NOT_FOUND(NOT_FOUND, "C_016", "대기한 일정을 찾을 수 없습니다."),
	NOT_MATCH_PASSWORD(BAD_REQUEST, "C_017", "확인 비밀번호가 일치하지 않습니다."),
	INVITE_LINK_NOT_FOUND(NOT_FOUND, "C_018", "초대링크를 찾을 수 없습니다."),
	INVITE_NAME_NOT_VALID(BAD_REQUEST, "C_019", "초대회원 이름이 유효하지 않습니다."),
	EXERCISE_NOT_FOUND(NOT_FOUND, "C_020", "해당 운동이 존재하지 않습니다."),
	PASSWORD_NOT_VALID(BAD_REQUEST, "C_019", "비밀번호가 일치하지 않습니다."),
	DATETIME_NOT_VALID(BAD_REQUEST, "C_019", "시작날짜와 종료날짜가 유효하지 않습니다."),
	COURSE_ALREADY_EXISTS(BAD_REQUEST, "C_020", "잔여 수강권이 존재합니다."),
	COURSE_NOT_FOUND(NOT_FOUND, "C_020", "수강권이 존재하지 않습니다."),
	LESSON_CNT_NOT_VALID(BAD_REQUEST, "C_020", "수강권 횟수가 유효하지 않습니다."),
	LIKE_ALREADY_EXISTS(BAD_REQUEST, "C_020", "이미 좋아요 하였습니다."),

	MEMBER_NAME_LENGTH_NOT_VALID(BAD_REQUEST, "C_020", "이름은 최소 2자 이상 입력해 주세요."),
	MEMBER_NAME_NOT_VALID(BAD_REQUEST, "C_021", "이름은 한글 또는 영어만 입력할 수 있습니다."),
	CONFIRM_PASSWORD_NOT_MATCHED(BAD_REQUEST, "C_022", "비밀번호가 일치하지 않습니다."),
	USERID_POLICY_VIOLATION(BAD_REQUEST, "C_023", "아이디에 한글을 포함할 수 없습니다."),
	PASSWORD_POLICY_VIOLATION(BAD_REQUEST, "C_023", "영문+숫자 조합 8자리 이상을 입력해 주세요."),
	GYM_NOT_FOUND(NOT_FOUND, "C_024", "등록된 헬스장이 없습니다."),
	GYM_DUPLICATION(BAD_REQUEST, "C_025", "중복된 헬스장이 존재합니다."),
	JOIN_CODE_NOT_VALID(BAD_REQUEST, "C_026", "인증코드가 일치하지 않습니다. 다시 확인해 주세요."),
	MEMBER_ID_NOT_VALID(BAD_REQUEST, "C_027", "영문 4자리 이상 입력해 주세요."),
	TRAINER_NOT_FOUND(NOT_FOUND, "C_028", "트레이너가 존재하지 않습니다."),
	LESSON_HISTORY_NOT_FOUND(NOT_FOUND, "C_029", "수업 내역이 존재하지 않습니다."),
	LESSON_HISTORY_COMMENT_NOT_FOUND(NOT_FOUND, "C_030", "수업 내역 댓글이 존재하지 않습니다."),
	EXCEED_MAXIMUM_NUMBER_OF_FILES(BAD_REQUEST, "C_031", "파일 업로드는 최대 3개까지 가능합니다."),

	SERVER_ERROR(INTERNAL_SERVER_ERROR, "S_001", "서버에서 오류가 발생하였습니다."),
	FILE_UPLOAD_ERROR(INTERNAL_SERVER_ERROR, "S_002", "파일 업로드중 에러가 발생하였습니다."),
	FILE_FIND_ERROR(INTERNAL_SERVER_ERROR, "S_003", "파일 조회중 에러가 발생하였습니다."),
	MAIL_SEND_ERROR(INTERNAL_SERVER_ERROR, "S_004", "메일 전송중 에러가 발생했습니다."),
	FILE_REMOVE_ERROR(INTERNAL_SERVER_ERROR, "S_005", "파일 삭제중 에러가 발생하였습니다."),
	NAVER_CONNECTION_ERROR(INTERNAL_SERVER_ERROR, "S_006", "네이버 소셜 서버와 연동중 에러가 발생하였습니다."),
	KAKAO_CONNECTION_ERROR(INTERNAL_SERVER_ERROR, "S_007", "카카오 소셜 서버와 연동중 에러가 발생하였습니다."),
	JSON_PARSING_ERROR(INTERNAL_SERVER_ERROR, "S_008", "JSON 토큰을 파싱중 에러가 발생하였습니다."),
	PROFILE_ACCESS_FAILED(INTERNAL_SERVER_ERROR, "S_009", "소셜 프로필을 가져오던 중 에러가 발생하였습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
