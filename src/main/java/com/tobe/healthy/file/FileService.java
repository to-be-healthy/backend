package com.tobe.healthy.file;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.tobe.healthy.common.RedisService;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.diet.domain.entity.Diet;
import com.tobe.healthy.diet.domain.entity.DietFiles;
import com.tobe.healthy.diet.domain.entity.DietType;
import com.tobe.healthy.diet.repository.DietFileRepository;
import com.tobe.healthy.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.tobe.healthy.common.RedisKeyPrefix.TEMP_FILE_URI;
import static com.tobe.healthy.config.error.ErrorCode.FILE_REMOVE_ERROR;
import static com.tobe.healthy.config.error.ErrorCode.FILE_UPLOAD_ERROR;
import static java.util.UUID.randomUUID;
import static org.springframework.util.StringUtils.cleanPath;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FileService {
	private final DietFileRepository dietFileRepository;
	private final AmazonS3 amazonS3;
	private final RedisService redisService;

	@Value("${aws.s3.bucket-name}")
	private String bucketName;

	private final Long FILE_TEMP_UPLOAD_TIMEOUT = 30 * 60 * 1000L; // 30분


	// 1. 파일을 AWS S3에 업로드 후 업로드 주소 반환
	public List<RegisterFile> uploadFiles(String folder, List<MultipartFile> uploadFiles, Member member) {
		List<RegisterFile> uploadFile = new ArrayList<>();
		int fileOrder = 0;
		for (MultipartFile file: uploadFiles) {
			if (!file.isEmpty()) {
				try (InputStream inputStream = file.getInputStream()) {
					String originalFileName = file.getOriginalFilename();
					String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
					ObjectMetadata objectMetadata = new ObjectMetadata();
					objectMetadata.setContentLength(file.getSize());
					objectMetadata.setContentType(file.getContentType());
					String savedFileName = folder + "/" + System.currentTimeMillis() + "-" + randomUUID() + extension;
					amazonS3.putObject(
						"to-be-healthy-bucket",
						savedFileName,
						inputStream,
						objectMetadata
					);
					String fileUrl = amazonS3.getUrl("to-be-healthy-bucket", savedFileName).toString();
					redisService.setValuesWithTimeout(TEMP_FILE_URI.getDescription() + fileUrl, member.getId().toString(), FILE_TEMP_UPLOAD_TIMEOUT); // 30분
					uploadFile.add(new RegisterFile(fileUrl, ++fileOrder));
				} catch (IOException e) {
					log.error("error => {}", e.getStackTrace()[0]);
				}
			}
		}
		return uploadFile;
	}

	public void deleteFile(String fileName){
		try{
			amazonS3.deleteObject(bucketName, fileName);
		}catch (Exception e){
			log.error("error => {}", e.getStackTrace()[0]);
			throw new CustomException(FILE_REMOVE_ERROR);
		}
	}

	public void uploadDietFile(Diet diet, DietType type, MultipartFile uploadFile) {
		if (!uploadFile.isEmpty()) {
			try {
				String savedFileName = System.currentTimeMillis() + "-" + randomUUID();
				String extension = Objects.requireNonNull(uploadFile.getOriginalFilename()).substring(uploadFile.getOriginalFilename().lastIndexOf("."));

				ObjectMetadata objectMetadata = new ObjectMetadata();
				objectMetadata.setContentLength(uploadFile.getSize());
				objectMetadata.setContentType(uploadFile.getContentType());
				amazonS3.putObject(bucketName, savedFileName, uploadFile.getInputStream(), objectMetadata);
				String fileUrl = amazonS3.getUrl(bucketName, savedFileName).toString();

				DietFiles dietFile = DietFiles.create(savedFileName, cleanPath(uploadFile.getOriginalFilename())
						, extension, uploadFile.getSize(), diet, fileUrl, type);
				dietFileRepository.save(dietFile);
			} catch (IOException e) {
				log.error("error => {}", e.getStackTrace()[0]);
				throw new CustomException(FILE_UPLOAD_ERROR);
			}
		}
	}
}
