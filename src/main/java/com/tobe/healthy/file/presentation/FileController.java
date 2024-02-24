package com.tobe.healthy.file.presentation;

import com.tobe.healthy.file.domain.dto.in.FileRegisterCommand;
import com.tobe.healthy.file.application.FileService;
import lombok.RequiredArgsConstructor;
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
	public ResponseEntity<?> retrieveFile(@RequestParam("fileId") Long fileId) throws Exception {
		return fileService.retrieveFile(fileId);
	}

	@PostMapping("/upload")
	public ResponseEntity<Long> registerFile(@RequestParam("file") MultipartFile file, FileRegisterCommand request) throws Exception {
		Long result = fileService.uploadFile(file, request);
		return ResponseEntity.ok(result);
	}
}