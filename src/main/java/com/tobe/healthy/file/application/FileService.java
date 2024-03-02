package com.tobe.healthy.file.application;


import static com.tobe.healthy.config.error.ErrorCode.FILE_FIND_ERROR;
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

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.file.domain.dto.in.FileRegisterCommand;
import com.tobe.healthy.file.domain.entity.Profile;
import com.tobe.healthy.file.domain.entity.WorkoutHistoryFile;
import com.tobe.healthy.file.repository.FileRepository;
import com.tobe.healthy.file.repository.WorkoutFileRepository;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import com.tobe.healthy.workout.domain.entity.WorkoutHistory;
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
	private final WorkoutFileRepository workoutFileRepository;
	private final MemberRepository memberRepository;

	@Value("${file.upload.location}")
	private String uploadDir;

	@Transactional
	public Boolean uploadFile(MultipartFile uploadFile, FileRegisterCommand request) {
		if (!uploadFile.isEmpty()) {
			try {
				String savedFileName = randomUUID().toString();
				String extension = Objects.requireNonNull(uploadFile.getOriginalFilename()).substring(uploadFile.getOriginalFilename().lastIndexOf("."));

				Path copyOfLocation = Paths.get(uploadDir + separator + cleanPath(savedFileName + extension));
				Files.copy(uploadFile.getInputStream(), copyOfLocation, REPLACE_EXISTING);

				Profile profile = Profile.create(savedFileName, cleanPath(uploadFile.getOriginalFilename()), extension, uploadDir + separator, uploadFile.getSize());
				Member member = memberRepository.findById(request.getMemberId())
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

			Profile profile = Profile.create(savedFileName, fileName, extension, uploadDir + separator, fileSize);

		} catch (IOException e) {
			throw new CustomException(SERVER_ERROR);
		}
		return true;
	}

	@Transactional
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

	@Transactional
	public void uploadWorkoutFiles(WorkoutHistory history, List<MultipartFile> files) {
		files.forEach(f -> uploadWorkoutFile(f, history));
	}

	@Transactional
	public void uploadWorkoutFile(MultipartFile uploadFile, WorkoutHistory history) {
		if (!uploadFile.isEmpty()) {
			try {
				String savedFileName = randomUUID().toString();
				String extension = Objects.requireNonNull(uploadFile.getOriginalFilename()).substring(uploadFile.getOriginalFilename().lastIndexOf("."));
				Path copyOfLocation = get(uploadDir + separator + cleanPath(savedFileName + extension));
				Files.createDirectories(copyOfLocation.getParent());
				Files.copy(uploadFile.getInputStream(), copyOfLocation, REPLACE_EXISTING);

				WorkoutHistoryFile historyFile = WorkoutHistoryFile.create(savedFileName,
						cleanPath(uploadFile.getOriginalFilename()), extension, uploadDir + separator, uploadFile.getSize(), history);
				workoutFileRepository.save(historyFile);
			} catch (IOException e) {
				e.printStackTrace();
				throw new CustomException(FILE_UPLOAD_ERROR);
			}
		}
	}
}
