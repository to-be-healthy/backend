package com.tobe.healthy.file.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.lessonHistory.domain.entity.LessonHistory;
import com.tobe.healthy.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

	private String fileUrl;

	private int fileOrder;

	public static AwsS3File create(String originalFileName, Member member, LessonHistory lessonHistory, String fileUrl, int fileOrder) {
		return AwsS3File.builder()
				.originalFileName(originalFileName)
				.member(member)
				.lessonHistory(lessonHistory)
				.fileUrl(fileUrl)
				.fileOrder(fileOrder)
				.build();
	}

	public static AwsS3File create(String originalFileName, Member member, String fileUrl, int fileOrder) {
		return AwsS3File.builder()
				.originalFileName(originalFileName)
				.member(member)
				.fileUrl(fileUrl)
				.fileOrder(fileOrder)
				.build();
	}
}