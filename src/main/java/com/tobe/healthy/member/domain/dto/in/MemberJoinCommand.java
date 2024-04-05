package com.tobe.healthy.member.domain.dto.in;

import com.tobe.healthy.member.domain.entity.MemberType;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "password")
@Schema(description = "회원가입 DTO")
@Builder
public class MemberJoinCommand {
    @Schema(description = "아이디", example = "to-be-healthy")
    @NotEmpty(message = "아이디를 입력해 주세요.")
    private String userId;

    @Parameter(description = "아이디", example = "to-be-healthy")
    @Schema(description = "이메일", example = "to-be-healthy@gmail.com")
    @NotEmpty(message = "이메일을 입력해 주세요.")
    private String email;

    @Schema(description = "비밀번호", example = "zxcvbnm11")
    @NotEmpty(message = "비밀번호를 입력해 주세요.")
    private String password;

    @Schema(description = "비밀번호 확인", example = "zxcvbnm11")
    @NotEmpty(message = "비밀번호를 재입력해 주세요.")
    private String passwordConfirm;

    @Schema(description = "실명", example = "홍길동")
    @NotEmpty(message = "실명을 입력해 주세요.")
    private String name;

    @Schema(description = "회원 구분", example = "STUDENT")
    @NotNull(message = "회원 구분이 필요합니다.")
    private MemberType memberType;

    @Schema(description = "초대링크로 가입하는 경우 uuid")
    private String uuid;
}
