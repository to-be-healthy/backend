package com.tobe.healthy.member.presentation.dto.in;

public record CommandAppleUserInfo(ApplerUserName name, String email) {

	public record ApplerUserName(String firstName, String lastName) {}
}
