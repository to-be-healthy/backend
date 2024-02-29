package com.tobe.healthy.member.domain.dto.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberFindIdCommand {
	@NotEmpty(message = "휴대폰번호를 입력해 주세요.")
	private String mobileNum;

	@NotEmpty(message = "닉네임을 입력해 주세요.")
	private String nickname;
}
