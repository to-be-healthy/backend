package com.tobe.healthy.member.domain.dto.in;

import com.tobe.healthy.lessonhistory.domain.dto.out.CommandUploadFileResult;
import lombok.Data;

@Data
public class CommandRegisterMemberProfile {
    private CommandUploadFileResult uploadFile;
}
