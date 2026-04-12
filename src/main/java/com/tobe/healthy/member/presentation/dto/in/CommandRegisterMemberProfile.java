package com.tobe.healthy.member.presentation.dto.in;

import com.tobe.healthy.lessonhistory.presentation.dto.out.CommandUploadFileResult;

public record CommandRegisterMemberProfile(
	CommandUploadFileResult uploadFile
) {
}
