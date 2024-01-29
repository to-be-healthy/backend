package com.tobe.healthy.file.presentation;

import com.tobe.healthy.file.application.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
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
		Resource result = fileService.retrieveFile(fileId);
		return ResponseEntity.ok()
			.contentType(MediaType.valueOf("image/png"))
			.body(result);
	}

	@PostMapping("/upload")
	public ResponseEntity<Long> registerFile(@RequestParam("file") MultipartFile file) throws Exception {
		Long result = fileService.uploadFile(file);
		return ResponseEntity.ok(result);
	}
}