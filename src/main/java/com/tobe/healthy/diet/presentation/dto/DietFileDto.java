package com.tobe.healthy.diet.presentation.dto;

import com.tobe.healthy.diet.domain.DietFiles;
import com.tobe.healthy.diet.domain.DietType;

public record DietFileDto(Long id, Long dietId, String fileUrl, DietType type) {

	public static DietFileDto from(DietFiles dietFile) {
		return new DietFileDto(
			dietFile.getId(),
			dietFile.getDiet().getDietId(),
			dietFile.getFileUrl(),
			dietFile.getType()
		);
	}
}
