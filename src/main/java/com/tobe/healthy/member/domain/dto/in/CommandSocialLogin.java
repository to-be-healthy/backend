package com.tobe.healthy.member.domain.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tobe.healthy.member.domain.entity.MemberType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "소셜 로그인 DTO")
public class CommandSocialLogin {
	@Schema(description = "인가코드(카카오, 네이버, 구글 소셜 로그인시 필요)", example = "인가코드(카카오, 네이버, 구글 소셜 로그인시 필요)")
    private String code;

	@Schema(description = "상태코드(CSRF 방지, 네이버 소셜 로그인시 필요)", example = "STATE_STRING(네이버 로그인시 필요)")
	private String state;

	@Schema(description = "회원구분(모든 소셜 로그인시 필요)", example = "STUDENT || TRAINER(모든 소셜 로그인시 필요)")
	private MemberType memberType;

	@Schema(description = "Redirect URL(카카오, 구글 소셜 로그인시 필요)", example = "http://localhost:3000/kakao/callback(카카오, 구글 소셜 로그인시 필요)")
	private String redirectUrl;

	@Schema(description = "초대링크로 가입하는 경우 uuid")
	private String uuid;

	@Schema(description = "애플 토큰 데이터")
	@JsonProperty("id_token")
	private String idToken;

	private CommandAppleUserInfo user;
}
