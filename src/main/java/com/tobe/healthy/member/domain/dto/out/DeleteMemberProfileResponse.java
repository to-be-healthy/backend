package com.tobe.healthy.member.domain.dto.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteMemberProfileResponse {
    private String fileUrl;
    private String fileName;

    public static DeleteMemberProfileResponse from(String fileUrl, String fileName) {
        return DeleteMemberProfileResponse.builder()
                .fileUrl(fileUrl)
                .fileName(fileName)
                .build();
    }
}
