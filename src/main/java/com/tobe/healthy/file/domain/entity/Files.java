package com.tobe.healthy.file.domain.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.member.domain.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
public class Files extends BaseTimeEntity<Files, Long> {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "files_id")
	private Long id;

	private String fileName;

	private String originalName;

	@Column(name = "file_ext")
	private String extension;

	@OneToOne(mappedBy = "files")
	private Member member;

	private String filePath;

	private long fileSize;

	private Integer fileCnt;

	public static Files create(String savedFileName, String originalName, String extension, String filePath, long fileSize, Integer fileCnt) {
		Files files = new Files();
		files.fileName = savedFileName;
		files.originalName = originalName;
		files.extension = extension;
		files.filePath = filePath;
		files.fileSize = fileSize;
		files.fileCnt = fileCnt;
		return files;
	}
}
