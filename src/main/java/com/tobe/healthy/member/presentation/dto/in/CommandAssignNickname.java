package com.tobe.healthy.member.presentation.dto.in;

import jakarta.validation.constraints.NotEmpty;

public record CommandAssignNickname(
	@NotEmpty String nickname
) {
}
