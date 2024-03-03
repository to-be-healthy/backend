package com.tobe.healthy.member.domain.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Schema(description = "이메일 인증번호 검증 DTO")
public class VerifyAuthMailRequest {
	@NotEmpty(message = "이메일을 입력해 주세요.")
	private String email;
	@NotEmpty(message = "이메일 인증번호를 입력해 주세요.")
	private String authKey;
}
