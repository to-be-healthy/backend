package com.tobe.healthy.diet.presentation.dto;

import com.tobe.healthy.diet.domain.DietFiles;
import com.tobe.healthy.diet.domain.DietType;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class DietFileDto {

	private Long id;
	private Long dietId;
	private String fileUrl;
	private DietType type;

	public static DietFileDto from(DietFiles dietFile) {
		return DietFileDto.builder()
			.id(dietFile.getId())
			.fileUrl(dietFile.getFileUrl())
			.type(dietFile.getType())
			.dietId(dietFile.getDiet().getDietId())
			.build();
	}
}
