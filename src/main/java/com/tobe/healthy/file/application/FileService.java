package com.tobe.healthy.file.application;


import static com.tobe.healthy.config.error.ErrorCode.FILE_UPLOAD_ERROR;
import static java.nio.file.Files.probeContentType;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.http.HttpStatus.OK;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.file.domain.dto.in.FileRegisterCommand;
import com.tobe.healthy.file.domain.entity.FileInfo;
import com.tobe.healthy.file.repository.FileRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

	private final FileRepository fileRepository;

	@Value("${file.upload.location}")
	private String uploadDir;

	@Transactional
	public Boolean uploadFile(MultipartFile uploadFile, FileRegisterCommand request) {
		if (!uploadFile.isEmpty()) {
			fileRepository.findByMemberId(request.getMemberId()).ifPresent(f -> {
				fileRepository.deleteAllByMemberId(request.getMemberId());
			});
			try {
				Path copyOfLocation = Paths.get(uploadDir + File.separator + StringUtils.cleanPath(uploadFile.getOriginalFilename()));
				Files.copy(uploadFile.getInputStream(), copyOfLocation, REPLACE_EXISTING);
				log.info("copyOfLocation : " + copyOfLocation);
				log.info("cleanPath : " + StringUtils.cleanPath(uploadFile.getOriginalFilename()));
				String extension = uploadFile.getOriginalFilename()
					.substring(uploadFile.getOriginalFilename().lastIndexOf("."));
				log.info("extension : " + extension);
				FileInfo entity = FileInfo.create(StringUtils.cleanPath(uploadFile.getOriginalFilename()), originalFileName, extension,"/", uploadFile.getSize(), 0);
				//


//				String originalFileName = uploadFile.getOriginalFilename();                                 // 오리지날 파일명
//				String extension = originalFileName.substring(originalFileName.lastIndexOf("."));    // 파일 확장자
//				String savedFileName = randomUUID().toString();                                             // 저장될 파일명
//				Files savedFile = Files.create(savedFileName, originalFileName, extension,"/", uploadFile.getSize(), 0);
//				uploadFile.transferTo(new File(savedFileName + extension));
//				fileRepository.save(savedFile);
			} catch (IOException e) {
				throw new CustomException(FILE_UPLOAD_ERROR);
			}
			return true;
		}
		return false;
	}

	@Transactional
	public ResponseEntity<Resource> retrieveFile(Long fileId) throws Exception {
		FileInfo entity = fileRepository.findById(fileId)
			.orElseThrow(() -> new IllegalArgumentException("저장된 파일이 없습니다."));

		String path = entity.getFilePath() + entity.getFileName() + entity.getExtension();
		Resource resource = new FileSystemResource(path);

		HttpHeaders httpHeaders = new HttpHeaders();
		Path filePath = get(path);
		httpHeaders.add("Content-Type", probeContentType(filePath));

		return new ResponseEntity<>(resource, httpHeaders, OK);
	}
}
