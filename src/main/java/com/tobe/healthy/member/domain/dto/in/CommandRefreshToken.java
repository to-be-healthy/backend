package com.tobe.healthy.member.domain.dto.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommandRefreshToken {
    @NotEmpty
    private String userId;

    @NotEmpty
    private String refreshToken;
}
