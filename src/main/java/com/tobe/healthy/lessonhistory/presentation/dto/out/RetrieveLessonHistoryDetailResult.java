package com.tobe.healthy.lessonhistory.presentation.dto.out;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.tobe.healthy.common.LessonTimeFormatter;
import com.tobe.healthy.lessonhistory.domain.entity.LessonAttendanceStatus;
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistory;
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryComment;
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryFiles;
import com.tobe.healthy.member.domain.entity.Member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "수업 일지 상세 조회 응답 DTO")
public class RetrieveLessonHistoryDetailResult {

	private Long id;
	private String title;
	private String content;
	@Builder.Default
	private List<LessonHistoryCommentCommandResult> comments = new ArrayList<>();
	private Integer commentTotalCount;
	private LocalDateTime createdAt;
	private String student;
	private String trainer;
	private Long scheduleId;
	private String lessonDt;
	private String lessonTime;
	private String attendanceStatus;
	@Builder.Default
	private List<LessonHistoryFileResults> files = new ArrayList<>();

	public static RetrieveLessonHistoryDetailResult detailFrom(LessonHistory entity) {
		if (entity == null) {
			return null;
		}
		String trainerName = entity.getTrainer() != null && entity.getTrainer().getName() != null
			? entity.getTrainer().getName() + " 트레이너" : null;

		return RetrieveLessonHistoryDetailResult.builder()
			.id(entity.getId())
			.title(entity.getTitle())
			.content(entity.getContent())
			.comments(sortLessonHistoryComment(entity.getLessonHistoryComment()))
			.commentTotalCount((int)entity.getLessonHistoryComment().stream().filter(c -> !c.isDelYn()).count())
			.createdAt(entity.getCreatedAt())
			.student(entity.getStudent() != null ? entity.getStudent().getName() : null)
			.trainer(trainerName)
			.scheduleId(entity.getSchedule() != null ? entity.getSchedule().getId() : null)
			.lessonDt(LessonTimeFormatter.formatLessonDt(
				entity.getSchedule() != null ? entity.getSchedule().getLessonDt() : null))
			.lessonTime(LessonTimeFormatter.formatLessonTime(
				entity.getSchedule() != null ? entity.getSchedule().getLessonStartTime() : null,
				entity.getSchedule() != null ? entity.getSchedule().getLessonEndTime() : null))
			.attendanceStatus(validateAttendanceStatus(
				entity.getSchedule() != null ? entity.getSchedule().getLessonDt() : null,
				entity.getSchedule() != null ? entity.getSchedule().getLessonEndTime() : null))
			.files(entity.getFiles().stream()
				.filter(f -> f.getLessonHistoryComment() == null)
				.map(LessonHistoryFileResults::from)
				.sorted(Comparator.comparing(LessonHistoryFileResults::getCreatedAt))
				.collect(Collectors.toList()))
			.build();
	}

	private static List<LessonHistoryCommentCommandResult> sortLessonHistoryComment(
		List<LessonHistoryComment> comments) {
		if (comments == null) {
			return new ArrayList<>();
		}

		List<LessonHistoryComment> sorted = comments.stream()
			.sorted(Comparator.comparingInt(LessonHistoryComment::getOrder))
			.collect(Collectors.toList());

		List<LessonHistoryComment> parentComments = sorted.stream()
			.filter(c -> c.getParent() == null)
			.collect(Collectors.toList());

		List<LessonHistoryComment> childComments = sorted.stream()
			.filter(c -> c.getParent() != null)
			.collect(Collectors.toList());

		for (LessonHistoryComment parentComment : parentComments) {
			List<LessonHistoryComment> replies = childComments.stream()
				.filter(child -> child.getParent().getId().equals(parentComment.getId()))
				.sorted(Comparator.comparingInt(LessonHistoryComment::getOrder))
				.collect(Collectors.toList());
			parentComment.setReplies(replies);
		}

		return parentComments.stream()
			.map(LessonHistoryCommentCommandResult::from)
			.collect(Collectors.toList());
	}

	private static String validateAttendanceStatus(LocalDate lessonDt, LocalTime lessonEndTime) {
		LocalDateTime lesson = LocalDateTime.of(lessonDt, lessonEndTime);
		if (LocalDateTime.now().isAfter(lesson)) {
			return LessonAttendanceStatus.ATTENDED.getDescription();
		}
		return LessonAttendanceStatus.ABSENT.getDescription();
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class LessonHistoryCommentCommandResult {
		private Long id;
		private String content;
		private LessonHistoryCommentMemberResult member;
		private Integer orderNum;
		private Long parentId;
		@Builder.Default
		private List<LessonHistoryCommentCommandResult> replies = new ArrayList<>();
		@Builder.Default
		private List<LessonHistoryFileResults> files = new ArrayList<>();
		private Boolean delYn;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;

		public static LessonHistoryCommentCommandResult from(LessonHistoryComment entity) {
			if (entity == null) {
				return null;
			}
			List<LessonHistoryCommentCommandResult> repliesList = entity.getReplies() != null
				? entity.getReplies().stream().map(LessonHistoryCommentCommandResult::from).collect(Collectors.toList())
				: new ArrayList<>();

			List<LessonHistoryFileResults> filesList = entity.getFiles() != null
				? entity.getFiles().stream().map(LessonHistoryFileResults::from).collect(Collectors.toList())
				: new ArrayList<>();

			return LessonHistoryCommentCommandResult.builder()
				.id(entity.getId())
				.content(entity.isDelYn() ? "삭제된 댓글입니다." : entity.getContent())
				.member(LessonHistoryCommentMemberResult.from(entity.getWriter()))
				.orderNum(entity.getOrder())
				.replies(repliesList)
				.parentId(entity.getParent() != null ? entity.getParent().getId() : null)
				.files(filesList)
				.delYn(entity.isDelYn())
				.createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt())
				.build();
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class LessonHistoryCommentMemberResult {
		private Long memberId;
		private String name;
		private String fileUrl;

		public static LessonHistoryCommentMemberResult from(Member entity) {
			if (entity == null) {
				return null;
			}
			return LessonHistoryCommentMemberResult.builder()
				.memberId(entity.getId())
				.name(entity.getName())
				.fileUrl(entity.getMemberProfile() != null ? entity.getMemberProfile().getFileUrl() : null)
				.build();
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class LessonHistoryFileResults {
		private String fileUrl;
		private Integer fileOrder;
		private LocalDateTime createdAt;

		public static LessonHistoryFileResults from(LessonHistoryFiles entity) {
			if (entity == null) {
				return null;
			}
			return LessonHistoryFileResults.builder()
				.fileUrl(entity.getFileUrl())
				.fileOrder(entity.getFileOrder())
				.createdAt(entity.getCreatedAt())
				.build();
		}
	}
}
