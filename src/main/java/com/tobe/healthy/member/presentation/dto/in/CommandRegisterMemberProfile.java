package com.tobe.healthy.member.presentation.dto.in;

import com.tobe.healthy.lessonhistory.presentation.dto.out.CommandUploadFileResult;

import lombok.Data;

@Data
public class CommandRegisterMemberProfile {
	private CommandUploadFileResult uploadFile;
}
