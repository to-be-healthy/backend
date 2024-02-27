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
public class FileInfo extends BaseTimeEntity<FileInfo, Long> {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "file_info_id")
	private Long id;

	private String fileName;

	private String originalName;

	@Column(name = "file_ext")
	private String extension;

	@OneToOne(mappedBy = "fileInfo")
	private Member member;

	private String filePath;

	private long fileSize;

	private Integer fileCnt;

	public static FileInfo create(String savedFileName, String originalName, String extension, String filePath, long fileSize, Integer fileCnt) {
		FileInfo entity = new FileInfo();
		entity.fileName = savedFileName;
		entity.originalName = originalName;
		entity.extension = extension;
		entity.filePath = filePath;
		entity.fileSize = fileSize;
		entity.fileCnt = fileCnt;
		return entity;
	}
}
