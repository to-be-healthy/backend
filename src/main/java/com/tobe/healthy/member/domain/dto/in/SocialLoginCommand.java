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
	@Schema(description = "인가코드(카카오, 네이버, 구글 소셜 로그인시 필요)")
    private String code;

	@Schema(description = "상태코드(CSRF 방지), 네이버 소셜 로그인시 필요")
	private String state;

	@Schema(description = "회원구분(모든 소셜 로그인시 필요)")
	private MemberType memberType;

	@Schema(description = "Redirect URL(카카오 소셜 로그인시 필요)")
	private String redirectUrl;
}
