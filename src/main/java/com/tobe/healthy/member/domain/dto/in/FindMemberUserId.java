package com.tobe.healthy.member.domain.dto.in;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberType;
import com.tobe.healthy.member.domain.entity.SocialType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "아이디 찾기 DTO")
public class FindMemberUserId {
	@Schema(description = "이메일" , example = "to-be-healthy@gmail.com")
	@NotEmpty(message = "이메일을 입력해 주세요.")
	@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "올바른 이메일 형식을 입력해 주세요.")
	private String email;

	@Schema(description = "실명" , example = "홍길동")
	@NotEmpty(message = "실명을 입력해 주세요.")
	private String name;

	@Schema(description = "회원 구분" , example = "STUDENT")
	@NotNull(message = "회원 구분이 필요합니다.")
	private MemberType memberType;

	@Data
    @ToString
	@AllArgsConstructor
	@Builder
	public static class FindMemberUserIdResult {

		private String userId;
		private LocalDateTime createdAt;
		private SocialType socialType;
		@JsonIgnore
		private String message;

		public static FindMemberUserIdResult from(Member member, String message) {
			return FindMemberUserIdResult.builder()
					.userId(member.getUserId().substring(0, member.getUserId().length() - 2) + "**")
					.createdAt(member.getCreatedAt())
					.socialType(member.getSocialType())
					.message(message)
					.build();
		}
	}
}
