package com.tobe.healthy.member.domain.dto.out;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class RegisterMemberProfileResult {
    private String fileUrl;
    private String fileName;

    public static RegisterMemberProfileResult from(String fileUrl, String fileName) {
        return RegisterMemberProfileResult.builder()
                .fileUrl(fileUrl)
                .fileName(fileName)
                .build();
    }
}
