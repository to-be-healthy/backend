package com.tobe.healthy.member.domain.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "비밀번호 변경 DTO")
public class CommandChangeMemberPassword {
	@Schema(description = "현재 비밀번호" , example = "zxcvbnm=1")
	@NotEmpty(message = "현재 비밀번호를 입력해 주세요.")
	private String currPassword1;

	@Schema(description = "현재 비밀번호 다시 입력" , example = "zxcvbnm=1")
	@NotEmpty(message = "현재 비밀번호를 입력해 주세요.")
	private String currPassword2;

	@Schema(description = "변경할 비밀번호" , example = "1=mnbvcxz")
	@NotEmpty(message = "변경할 비밀번호를 입력해 주세요.")
	private String changePassword;
}
