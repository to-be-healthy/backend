package com.tobe.healthy.file.presentation;

import com.tobe.healthy.file.application.FileService;
import com.tobe.healthy.file.domain.dto.in.FileRegisterCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

	private final FileService fileService;

	@GetMapping("/display")
	public ResponseEntity<Resource> retrieveFile(@RequestParam("fileId") Long fileId) throws Exception {
		return fileService.retrieveFile(fileId);
	}

	@PostMapping("/upload")
	public ResponseEntity<Boolean> registerFile(@RequestParam("file") MultipartFile file, FileRegisterCommand request) {
		return ResponseEntity.ok(fileService.uploadFile(file, request));
	}
}