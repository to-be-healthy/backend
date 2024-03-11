package com.tobe.healthy.member.domain.dto.in;

import com.tobe.healthy.member.domain.entity.MemberType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MemberJoinCommand {
    @NotEmpty(message = "아이디를 입력해 주세요.")
    private String userId;

    @NotEmpty(message = "이메일을 입력해 주세요.")
    private String email;

    @NotEmpty(message = "비밀번호를 입력해 주세요.")
    private String password;

    @NotEmpty(message = "실명을 입력해 주세요.")
    private String name;

    @NotNull(message = "회원 구분이 필요합니다.")
    private MemberType memberType;
}
