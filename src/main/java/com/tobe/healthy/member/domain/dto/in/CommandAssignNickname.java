package com.tobe.healthy.member.domain.dto.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CommandAssignNickname {
    @NotEmpty
    private String nickname;
}
