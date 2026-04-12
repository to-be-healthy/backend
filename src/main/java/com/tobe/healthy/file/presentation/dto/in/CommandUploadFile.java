package com.tobe.healthy.file.presentation.dto.in;

import java.util.List;

public record CommandUploadFile(
	List<String> fileNames
) {
}
