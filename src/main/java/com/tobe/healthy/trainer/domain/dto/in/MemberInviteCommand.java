package com.tobe.healthy.trainer.domain.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MemberInviteCommand {

    @Schema(description = "아이디" , example = "to-be-healthy")
    @NotEmpty(message = "초대할 이메일을 추가해 주세요.")
    private List<String> emails = new ArrayList<>();

}
