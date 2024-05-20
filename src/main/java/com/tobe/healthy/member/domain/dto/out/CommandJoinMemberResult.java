package com.tobe.healthy.member.domain.dto.out;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "회원가입 성공 응답")
public class CommandJoinMemberResult {
	@Schema(description = "회원 ID")
    private Long id;
	@Schema(description = "이메일")
    private String email;
	@Schema(description = "아이디")
    private String userId;
	@Schema(description = "이름")
    private String name;
	@Schema(description = "회원구분")
    private MemberType memberType;

    public static CommandJoinMemberResult from(Member member){
        return CommandJoinMemberResult.builder()
                .id(member.getId())
                .email(member.getEmail())
                .userId(member.getUserId())
                .name(member.getName())
                .memberType(member.getMemberType())
                .build();
    }
}
