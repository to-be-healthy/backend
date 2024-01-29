package com.tobe.healthy.member.domain.dto.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberRegisterCommand {
    @NotEmpty(message = "이메일을 입력해 주세요.")
    private String email;

    @NotEmpty(message = "비밀번호를 입력해 주세요.")
    private String password;

    @NotEmpty(message = "별명을 입력해 주세요.")
    private String nickname;

}
