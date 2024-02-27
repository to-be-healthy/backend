package com.tobe.healthy.member.presentation;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberFindPWCommand {
	@NotEmpty(message = "닉네임을 입력해 주세요.")
	private String mobileNum;

	@NotEmpty(message = "이메일을 입력해 주세요.")
	private String email;
}
