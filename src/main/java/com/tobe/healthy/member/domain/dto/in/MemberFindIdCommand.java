package com.tobe.healthy.member.domain.dto.in;

import com.tobe.healthy.member.domain.entity.MemberType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "아이디 찾기 DTO")
public class MemberFindIdCommand {
	@Schema(description = "이메일" , example = "to-be-healthy@gmail.com")
	@NotEmpty(message = "이메일을 입력해 주세요.")
	private String email;

	@Schema(description = "실명" , example = "홍길동")
	@NotEmpty(message = "실명을 입력해 주세요.")
	private String name;

	@Schema(description = "회원 구분" , example = "MEMBER")
	@NotNull(message = "회원 구분이 필요합니다.")
	private MemberType memberType;

	@Data
	@AllArgsConstructor
	public static class MemberFindIdCommandResult {
		private String userId;
		private LocalDateTime createdAt;
	}
}
