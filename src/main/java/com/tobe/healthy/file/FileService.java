package com.tobe.healthy.file;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.diet.domain.entity.Diet;
import com.tobe.healthy.diet.domain.entity.DietFiles;
import com.tobe.healthy.diet.domain.entity.DietType;
import com.tobe.healthy.diet.repository.DietFileRepository;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberProfile;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.workout.domain.entity.WorkoutHistory;
import com.tobe.healthy.workout.domain.entity.WorkoutHistoryFiles;
import com.tobe.healthy.workout.repository.WorkoutFileRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.tobe.healthy.config.error.ErrorCode.*;
import static java.io.File.separator;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.UUID.randomUUID;
import static org.springframework.util.StringUtils.cleanPath;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FileService {

	private final WorkoutFileRepository workoutFileRepository;
	private final MemberRepository memberRepository;
	private final DietFileRepository dietFileRepository;
	private final AmazonS3 amazonS3;

	@Value("${file.upload.location}")
	private String uploadDir;

	@Value("${aws.s3.bucket-name}")
	private String bucketName;

	public Boolean uploadFile(MultipartFile uploadFile, Long memberId) {
		if (!uploadFile.isEmpty()) {
			try {
				String savedFileName = System.currentTimeMillis() + "_" + randomUUID();
				String extension = Objects.requireNonNull(uploadFile.getOriginalFilename()).substring(uploadFile.getOriginalFilename().lastIndexOf("."));

				Path copyOfLocation = Paths.get(uploadDir + separator + cleanPath(savedFileName + extension));
				Files.copy(uploadFile.getInputStream(), copyOfLocation, REPLACE_EXISTING);


				Member member = memberRepository.findById(memberId)
					.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

				MemberProfile memberProfile = MemberProfile.create("", member);

				member.registerProfile(memberProfile);

			} catch (IOException e) {
				throw new CustomException(FILE_UPLOAD_ERROR);
			}
			return true;
		}
		return false;
	}

	public void uploadWorkoutFiles(WorkoutHistory history, List<MultipartFile> files) {
		files.forEach(f -> uploadWorkoutFile(f, history));
	}

	public void uploadWorkoutFile(MultipartFile uploadFile, WorkoutHistory history) {
		if (!uploadFile.isEmpty()) {
			try {
				String savedFileName = System.currentTimeMillis() + "_" + randomUUID();
				String extension = Objects.requireNonNull(uploadFile.getOriginalFilename()).substring(uploadFile.getOriginalFilename().lastIndexOf("."));

				ObjectMetadata objectMetadata = new ObjectMetadata();
				objectMetadata.setContentLength(uploadFile.getSize());
				objectMetadata.setContentType(uploadFile.getContentType());
				amazonS3.putObject(bucketName, savedFileName, uploadFile.getInputStream(), objectMetadata);
				String fileUrl = amazonS3.getUrl(bucketName, savedFileName).toString();

				WorkoutHistoryFiles historyFile = WorkoutHistoryFiles.create(savedFileName,
						cleanPath(uploadFile.getOriginalFilename()), extension, uploadFile.getSize(), history, fileUrl);
				workoutFileRepository.save(historyFile);
			} catch (IOException e) {
				e.printStackTrace();
				throw new CustomException(FILE_UPLOAD_ERROR);
			}
		}
	}

	public void deleteFile(String fileName){
		try{
			amazonS3.deleteObject(bucketName, fileName);
		}catch (Exception e){
			e.printStackTrace();
			throw new CustomException(FILE_REMOVE_ERROR);
		}
	}

	// 1. 파일을 AWS S3에 업로드 후 업로드 주소 반환
	// 2.
	public List<RegisterFileResponse> uploadFiles(FileUploadType fileUploadType, List<MultipartFile> uploadFiles) {
		List<RegisterFileResponse> uploadFile = new ArrayList<>();
		int fileOrder = 0;
		for (MultipartFile file: uploadFiles) {
//			if (!file.isEmpty()) {
//				try (InputStream inputStream = file.getInputStream()) {
//					String originalFileName = file.getOriginalFilename();
//					String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
//					ObjectMetadata objectMetadata = new ObjectMetadata();
//					objectMetadata.setContentLength(file.getSize());
//					objectMetadata.setContentType(file.getContentType());
//					String savedFileName = fileUploadType.getCode() + separator + System.currentTimeMillis() + "_" + randomUUID() + extension;
//					amazonS3.putObject(
//						"to-be-healthy-bucket",
//						savedFileName,
//						inputStream,
//						objectMetadata
//					);
//					String fileUrl = amazonS3.getUrl("to-be-healthy-bucket", savedFileName).toString();
//					AwsS3File awsS3File = AwsS3File.builder()
//						.originalFileName(originalFileName)
//						.member(member)
//						.fileUploadType(fileUploadType)
//						.fileUploadTypeId(fileUploadTypeId)
//						.fileUrl(fileUrl)
//						.fileOrder(++fileOrder)
//						.build();
//
//					uploadFile.add(new RegisterFileResponse(fileUrl, originalFileName, fileOrder));
//				} catch (IOException e) {
//					e.printStackTrace();
//					log.error("error => {}", e.getMessage());
//				}
//			}
		}
		return uploadFile;
	}

	public void uploadDietFile(Diet diet, DietType type, MultipartFile uploadFile) {
		if (!uploadFile.isEmpty()) {
			try {
				String savedFileName = System.currentTimeMillis() + "_" + randomUUID();
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
				e.printStackTrace();
				throw new CustomException(FILE_UPLOAD_ERROR);
			}
		}
	}
}
