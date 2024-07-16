package com.tobe.healthy.member.domain.dto.in;

import lombok.Data;

@Data
public class CommandAppleUserInfo {
	private ApplerUserName name;
	private String email;

	@Data
	public static class ApplerUserName {
		private String firstName;
		private String lastName;
	}
}
