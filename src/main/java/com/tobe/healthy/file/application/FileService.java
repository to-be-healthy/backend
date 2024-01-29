package com.tobe.healthy.file.application;


import com.tobe.healthy.file.domain.Files;
import com.tobe.healthy.file.repository.FileRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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
	public Long uploadFile(MultipartFile uploadFile) throws Exception {

		Files savedFile = null;

		if (!uploadFile.isEmpty()) {
			String originalFileName = uploadFile.getOriginalFilename();    							 // 오리지날 파일명
			String extension = originalFileName.substring(originalFileName.lastIndexOf("."));    // 파일 확장자
			String savedFileName = UUID.randomUUID().toString();    					 			 // 저장될 파일명
			String fileDir = getFolder();

			savedFile = Files.create(savedFileName, originalFileName, extension, fileDir + "/", uploadFile.getSize(), 0);
			uploadFile.transferTo(new File(fileDir + "/" + savedFileName));
		}
		return fileRepository.save(savedFile).getId();

	}

	@Transactional
	public Resource retrieveFile(Long fileId) throws Exception {
		Files files = fileRepository.findById(fileId).orElse(null);
		Path path = Paths.get(files.getFilePath()).resolve(files.getFilePath());
		Resource resource = new UrlResource(path.toUri());
		log.info("resource = {}", resource);
		log.info("path = {}", path);
		return resource;
	}

	private String getFolder() throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String str = uploadDir + "/" + sdf.format(date);
		File folder = new File(str);
		if (!folder.exists()) {
			FileUtils.forceMkdir(folder);
		}
		return str;
	}
}
