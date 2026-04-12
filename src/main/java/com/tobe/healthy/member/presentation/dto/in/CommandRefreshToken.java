package com.tobe.healthy.member.presentation.dto.in;

import jakarta.validation.constraints.NotEmpty;

public record CommandRefreshToken(
	@NotEmpty String userId,
	@NotEmpty String refreshToken
) {
}
