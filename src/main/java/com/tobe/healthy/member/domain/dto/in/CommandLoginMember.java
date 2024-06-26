package com.tobe.healthy.member.domain.dto.in;

import com.tobe.healthy.member.domain.entity.MemberType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(exclude = "password")
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "로그인 DTO")
public class CommandLoginMember {
    @Schema(description = "아이디", example = "healthy-trainer0")
    @NotEmpty(message = "아이디를 입력해 주세요.")
    private String userId;

    @Schema(description = "비밀번호", example = "12345678a")
    @NotEmpty(message = "비밀번호를 입력해 주세요.")
    private String password;

    @Schema(description = "회원 구분" , example = "TRAINER")
    @NotNull(message = "회원 구분이 필요합니다.")
    private MemberType memberType;
}
