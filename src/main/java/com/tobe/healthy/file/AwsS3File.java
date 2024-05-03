package com.tobe.healthy.file;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.lesson_history.domain.entity.LessonHistory;
import com.tobe.healthy.lesson_history.domain.entity.LessonHistoryComment;
import com.tobe.healthy.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

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
