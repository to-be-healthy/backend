package com.tobe.healthy.file.presentation;

import com.tobe.healthy.file.application.FileService;
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
@RequestMapping("/v1/api/file")
public class FileController {

	private final FileService fileService;

	@PostMapping("/upload")
	public ResponseEntity<Boolean> registerFile(@RequestParam("file") MultipartFile file, Long memberId) {
		return ResponseEntity.ok(fileService.uploadFile(file, memberId));
	}

	@GetMapping("/display")
	public ResponseEntity<Resource> retrieveFile(@RequestParam("fileId") Long fileId) {
		return fileService.retrieveFile(fileId);
	}
}