package com.tobe.healthy.member.domain.dto.in;

import com.tobe.healthy.member.domain.entity.MemberType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "소셜 로그인 DTO")
public class SocialLoginCommand {
	@Schema(description = "인가코드")
    private String code;

	@Schema(description = "상태코드(CSRF 방지)")
	private String state;

	@Schema(description = "회원구분")
	private MemberType memberType;

	@Schema(description = "redirect URL")
	private String redirectUrl;
}
