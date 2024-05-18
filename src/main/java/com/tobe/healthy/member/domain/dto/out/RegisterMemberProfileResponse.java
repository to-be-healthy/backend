package com.tobe.healthy.member.domain.dto.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterMemberProfileResponse {
    private String fileUrl;
    private String fileName;

    public static RegisterMemberProfileResponse from(String fileUrl, String fileName) {
        return RegisterMemberProfileResponse.builder()
                .fileUrl(fileUrl)
                .fileName(fileName)
                .build();
    }
}
