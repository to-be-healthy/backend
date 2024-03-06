package com.tobe.healthy.member.domain.dto.in;

import com.tobe.healthy.member.domain.entity.MemberType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MemberJoinCommand {
    @NotEmpty(message = "이메일을 입력해 주세요.")
    private String email;

    @NotEmpty(message = "비밀번호를 입력해 주세요.")
    private String password;

    @NotEmpty(message = "별명을 입력해 주세요.")
    private String nickname;

    @NotNull(message = "회원 구분이 필요합니다.")
    private MemberType memberType;

    @NotEmpty(message = "휴대폰 번호를 입력해 주세요.")
    private String mobileNum;
}
