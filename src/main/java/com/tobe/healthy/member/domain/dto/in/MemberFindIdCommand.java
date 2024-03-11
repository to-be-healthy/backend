package com.tobe.healthy.member.domain.dto.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberFindIdCommand {
	@NotEmpty(message = "이메일을 입력해 주세요.")
	private String email;

	@NotEmpty(message = "실명을 입력해 주세요.")
	private String name;
}
