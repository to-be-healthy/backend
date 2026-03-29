package com.tobe.healthy.member.presentation.dto.out;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class DeleteMemberProfileResult {
	private String fileUrl;
	private String fileName;

	public static DeleteMemberProfileResult from(String fileUrl, String fileName) {
		return DeleteMemberProfileResult.builder()
			.fileUrl(fileUrl)
			.fileName(fileName)
			.build();
	}
}
