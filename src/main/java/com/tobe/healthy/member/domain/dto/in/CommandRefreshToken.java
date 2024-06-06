package com.tobe.healthy.member.domain.dto.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CommandRefreshToken {
    @NotEmpty
    private String userId;

    @NotEmpty
    private String refreshToken;
}
