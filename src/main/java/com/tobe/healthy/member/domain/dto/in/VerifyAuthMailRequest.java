package com.tobe.healthy.member.domain.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "이메일 인증번호 검증 DTO")
public class VerifyAuthMailRequest {
	private String email;
	private String authKey;
}
