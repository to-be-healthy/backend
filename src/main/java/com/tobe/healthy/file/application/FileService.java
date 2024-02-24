package com.tobe.healthy.file.application;


import static java.nio.file.Files.probeContentType;
import static java.nio.file.Paths.get;
import static java.util.UUID.randomUUID;
import static org.springframework.http.HttpStatus.OK;

import com.tobe.healthy.file.domain.dto.in.FileRegisterCommand;
import com.tobe.healthy.file.domain.entity.Files;
import com.tobe.healthy.file.repository.FileRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

	private final FileRepository fileRepository;

	@Value("${file.upload.location}")
	private String uploadDir;

	@Transactional
	public Long uploadFile(MultipartFile uploadFile, FileRegisterCommand request) throws Exception {

		Files savedFile = null;
		if (!uploadFile.isEmpty()) {
			if (fileRepository.findByMemberId(request.getMemberId()).isPresent()) {
				fileRepository.deleteAllByMemberId(request.getMemberId());
			}
			String originalFileName = uploadFile.getOriginalFilename();                                 // 오리지날 파일명
			String extension = originalFileName.substring(originalFileName.lastIndexOf("."));    // 파일 확장자
			String savedFileName = randomUUID().toString();                                             // 저장될 파일명
			String fileDir = getFolder();
			savedFile = Files.create(savedFileName, originalFileName, extension, fileDir + "/", uploadFile.getSize(), 0);
			uploadFile.transferTo(new File(fileDir + "/" + savedFileName));
		}
		return fileRepository.save(savedFile).getId();

	}

	@Transactional
	public ResponseEntity<?> retrieveFile(Long fileId) throws Exception {
		Files files = fileRepository.findById(fileId)
			.orElseThrow(() -> new IllegalArgumentException("저장된 파일이 없습니다."));

		String path = files.getFilePath() + files.getFileName() + files.getExtension();
		Resource resource = new FileSystemResource(path);

		HttpHeaders httpHeaders = new HttpHeaders();
		Path filePath = get(path);
		httpHeaders.add("Content-Type", probeContentType(filePath));

		return new ResponseEntity<>(resource, httpHeaders, OK);
	}

	private String getFolder() throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String str = uploadDir + "/" + sdf.format(date);
		Path path = Paths.get(str);
		if (java.nio.file.Files.exists(path)) {
			java.nio.file.Files.createDirectories(path);
		}
		return str;
	}
}
