package com.tobe.healthy.file.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.file.application.FileService;
import com.tobe.healthy.file.application.FileService.RegisterFileResponse;
import com.tobe.healthy.file.domain.entity.FileUploadType;
import io.swagger.v3.oas.annotations.Hidden;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/file")
@Hidden
public class FileController {

	private final FileService fileService;

	@PostMapping("/{fileUploadType}/{fileUploadTypeId}")
	public ResponseHandler<List<RegisterFileResponse>> registerFileToAwsS3(
												@PathVariable FileUploadType fileUploadType,
												@PathVariable Long fileUploadTypeId,
												List<MultipartFile> uploadFiles,
												@AuthenticationPrincipal CustomMemberDetails member) {
		return ResponseHandler.<List<RegisterFileResponse>>builder()
			.data(fileService.uploadFiles(fileUploadType, fileUploadTypeId, uploadFiles, member.getMemberId()))
			.message("파일 등록이 완료되었습니다.")
			.build();
	}

	@PostMapping("/upload")
	public ResponseEntity<Boolean> registerFile(@RequestParam("file") MultipartFile file, Long memberId) {
		return ResponseEntity.ok(fileService.uploadFile(file, memberId));
	}

	@GetMapping("/display")
	public ResponseEntity<Resource> retrieveFile(@RequestParam("fileId") Long fileId) {
		return fileService.retrieveFile(fileId);
	}
}