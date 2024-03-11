package com.tobe.healthy.member.domain.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberLoginCommand {
    @Schema(description = "아이디")
    @NotEmpty(message = "아이디를 입력해 주세요.")
    private String userId;

    @Schema(description = "비밀번호")
    @NotEmpty(message = "비밀번호를 입력해 주세요.")
    private String password;
}
