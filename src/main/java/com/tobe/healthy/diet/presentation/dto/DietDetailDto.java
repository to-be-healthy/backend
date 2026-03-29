package com.tobe.healthy.diet.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietDetailDto {

	@Builder.Default
	private Boolean fast = false;
	private DietFileDto dietFile;

	public DietDetailDto(Boolean fast) {
		this.fast = fast;
	}

}
