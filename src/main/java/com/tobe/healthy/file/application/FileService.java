package com.tobe.healthy.file.application;


import static com.tobe.healthy.config.error.ErrorCode.FILE_FIND_ERROR;
import static com.tobe.healthy.config.error.ErrorCode.FILE_REMOVE_ERROR;
import static com.tobe.healthy.config.error.ErrorCode.FILE_UPLOAD_ERROR;
import static com.tobe.healthy.config.error.ErrorCode.MEMBER_NOT_FOUND;
import static com.tobe.healthy.config.error.ErrorCode.SERVER_ERROR;
import static java.io.File.separator;
import static java.nio.file.Files.probeContentType;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.UUID.randomUUID;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.util.StringUtils.cleanPath;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.diet.domain.entity.Diet;
import com.tobe.healthy.file.domain.entity.AwsS3File;
import com.tobe.healthy.file.domain.entity.DietFile;
import com.tobe.healthy.file.domain.entity.DietType;
import com.tobe.healthy.file.domain.entity.FileUploadType;
import com.tobe.healthy.file.domain.entity.Profile;
import com.tobe.healthy.file.domain.entity.WorkoutHistoryFile;
import com.tobe.healthy.file.repository.AwsS3FileRepository;
import com.tobe.healthy.file.repository.DietFileRepository;
import com.tobe.healthy.file.repository.FileRepository;
import com.tobe.healthy.file.repository.WorkoutFileRepository;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.workout.domain.entity.WorkoutHistory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
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
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FileService {

	private final FileRepository fileRepository;
	private final WorkoutFileRepository workoutFileRepository;
	private final MemberRepository memberRepository;
	private final DietFileRepository dietFileRepository;
	private final AmazonS3 amazonS3;
	private final AwsS3FileRepository awsS3FileRepository;

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

				Profile profile = Profile.create(savedFileName, cleanPath(uploadFile.getOriginalFilename()), extension, uploadDir + separator, (int) uploadFile.getSize());

				Member member = memberRepository.findById(memberId)
					.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

				member.registerProfile(profile);
				fileRepository.save(profile);

			} catch (IOException e) {
				throw new CustomException(FILE_UPLOAD_ERROR);
			}
			return true;
		}
		return false;
	}

	public Boolean uploadFile(byte[] image, String profileImage) {
		try {
			String fileFullName = profileImage.substring(profileImage.lastIndexOf("/") + 1);

			String extension = fileFullName.substring(fileFullName.lastIndexOf("."));
			String savedFileName = randomUUID().toString();
			Path copyOfLocation = Paths.get(uploadDir + separator + savedFileName + extension);
			Files.copy(new ByteArrayInputStream(image), copyOfLocation, REPLACE_EXISTING);

			// 파일의 용량 구하기
			long fileSize = Files.size(copyOfLocation);
			String fileName = fileFullName.substring(0, fileFullName.lastIndexOf("."));
			Profile profile = Profile.create(savedFileName, fileName, extension, uploadDir + separator, (int) fileSize);

		} catch (IOException e) {
			throw new CustomException(SERVER_ERROR);
		}
		return true;
	}

	public ResponseEntity<Resource> retrieveFile(Long memberId) {
		Profile entity = fileRepository.findByMemberId(memberId)
			.orElseThrow(() -> new IllegalArgumentException("저장된 파일이 없습니다."));

		String path = entity.getFilePath() + entity.getFileName() + entity.getExtension();
		Resource resource = new FileSystemResource(path);
		HttpHeaders httpHeaders = new HttpHeaders();
		Path filePath = get(path);

		try {
			httpHeaders.add("Content-Type", probeContentType(filePath));
		} catch (Exception e) {
			throw new CustomException(FILE_FIND_ERROR);
		}

		return new ResponseEntity<>(resource, httpHeaders, OK);
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

				WorkoutHistoryFile historyFile = WorkoutHistoryFile.create(savedFileName,
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
	public List<RegisterFileResponse> uploadFiles(FileUploadType fileUploadType, Long fileUploadTypeId, List<MultipartFile> uploadFiles, Long memberId) {
		List<RegisterFileResponse> uploadFile = new ArrayList<>();
		Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
		int fileOrder = 0;
		for (MultipartFile file: uploadFiles) {
			if (!file.isEmpty()) {
				try (InputStream inputStream = file.getInputStream()) {
					String originalFileName = file.getOriginalFilename();
					String extension = originalFileName.substring(
						originalFileName.lastIndexOf("."));
					ObjectMetadata objectMetadata = new ObjectMetadata();
					objectMetadata.setContentLength(file.getSize());
					objectMetadata.setContentType(file.getContentType());
					String savedFileName = System.currentTimeMillis() + extension;
					amazonS3.putObject(
						"to-be-healthy-bucket",
						savedFileName,
						inputStream,
						objectMetadata
					);
					String fileUrl = amazonS3.getUrl("to-be-healthy-bucket", savedFileName).toString();
					AwsS3File awsS3File = AwsS3File.builder()
						.originalFileName(originalFileName)
						.member(member)
						.fileUploadType(fileUploadType)
						.fileUploadTypeId(fileUploadTypeId)
						.fileUrl(fileUrl)
						.fileOrder(++fileOrder)
						.build();
					awsS3FileRepository.save(awsS3File);

					uploadFile.add(new RegisterFileResponse(fileUrl, originalFileName, fileOrder));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return uploadFile;
	}

	@Data
	@AllArgsConstructor
	public static class RegisterFileResponse {
		private String fileUrl;
		private String fileName;
		private int fileOrder;
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

				DietFile dietFile = DietFile.create(savedFileName, cleanPath(uploadFile.getOriginalFilename())
						, extension, uploadFile.getSize(), diet, fileUrl, type);
				dietFileRepository.save(dietFile);
			} catch (IOException e) {
				e.printStackTrace();
				throw new CustomException(FILE_UPLOAD_ERROR);
			}
		}
	}
}
