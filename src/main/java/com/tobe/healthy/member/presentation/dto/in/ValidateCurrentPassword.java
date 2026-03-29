package com.tobe.healthy.member.presentation.dto.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ValidateCurrentPassword {
	@NotEmpty(message = "현재 비밀번호를 입력해 주세요.")
	private String password;
}
