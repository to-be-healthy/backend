package com.tobe.healthy.file.domain.entity;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.lessonHistory.domain.entity.LessonHistory;
import com.tobe.healthy.lessonHistory.domain.entity.LessonHistoryComment;
import com.tobe.healthy.member.domain.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class AwsS3File extends BaseTimeEntity<AwsS3File, Long> {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "file_id")
	private Long id;

	private String originalFileName;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "lesson_history_id")
	private LessonHistory lessonHistory;

	@ManyToOne(fetch = LAZY, cascade = ALL)
	@JoinColumn(name = "lesson_history_comment_id")
	private LessonHistoryComment lessonHistoryComment;

	private String fileUrl;

	private int fileOrder;

	@Enumerated(STRING)
	private FileUploadType fileUploadType;

	private Long fileUploadTypeId;

	public static AwsS3File create(String originalFileName, Member member, String fileUrl, int fileOrder) {
		return AwsS3File.builder()
				.originalFileName(originalFileName)
				.member(member)
				.fileUrl(fileUrl)
				.fileOrder(fileOrder)
				.build();
	}
}
