package com.tobe.healthy.member.domain.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "비밀번호 변경 DTO")
public class CommandChangeMemberPassword {
	@Schema(description = "변경할 비밀번호" , example = "12345678aaa")
	@NotEmpty(message = "변경할 비밀번호를 입력해 주세요.")
	private String changePassword1;

	@Schema(description = "변경할 비밀번호" , example = "12345678aaa")
	@NotEmpty(message = "변경할 비밀번호를 다시 입력해 주세요.")
	private String changePassword2;
}
