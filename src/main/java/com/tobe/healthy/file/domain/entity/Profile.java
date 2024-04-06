package com.tobe.healthy.file.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.lessonHistory.domain.entity.LessonHistory;
import com.tobe.healthy.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class Profile extends BaseTimeEntity<Profile, Long> {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "profile_id")
	private Long id;

	private String fileName;

	private String originalName;

	@Column(name = "file_ext")
	private String extension;

	@OneToOne(mappedBy = "profileId")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lessonHistory")
	private LessonHistory lessonHistory;

	private String filePath;

	private int fileSize;

	public static Profile create(String savedFileName, String originalName, String extension, String filePath, int fileSize) {
		return Profile.builder()
			.fileName(savedFileName)
			.originalName(originalName)
			.extension(extension)
			.filePath(filePath)
			.fileSize(fileSize)
			.build();
	}
}