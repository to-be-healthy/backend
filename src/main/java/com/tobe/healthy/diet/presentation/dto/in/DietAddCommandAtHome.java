package com.tobe.healthy.diet.presentation.dto.in;

import com.tobe.healthy.diet.domain.DietType;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DietAddCommandAtHome {

	private DietType type;
	private String file;
	private boolean fast;
	private String eatDate;

}
