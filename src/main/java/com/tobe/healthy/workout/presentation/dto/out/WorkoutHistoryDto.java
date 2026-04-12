package com.tobe.healthy.workout.presentation.dto.out;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import com.tobe.healthy.member.presentation.dto.MemberDto;
import com.tobe.healthy.member.domain.Member;
import com.tobe.healthy.member.domain.MemberProfile;
import com.tobe.healthy.workout.presentation.dto.CompletedExerciseDto;
import com.tobe.healthy.workout.presentation.dto.WorkoutHistoryFileDto;
import com.tobe.healthy.workout.presentation.dto.in.HistoryAddCommand;
import com.tobe.healthy.workout.domain.WorkoutHistory;
import org.springframework.web.multipart.MultipartFile;

public record WorkoutHistoryDto(
	Long workoutHistoryId,
	String content,
	MemberDto member,
	boolean liked,
	Long likeCnt,
	Long commentCnt,
	boolean viewMySelf,
	LocalDateTime createdAt,
	@JsonIgnore List<MultipartFile> multipartFiles,
	List<WorkoutHistoryFileDto> files,
	List<CompletedExerciseDto> completedExercises
) {

	@QueryProjection
	public WorkoutHistoryDto(Long workoutHistoryId, String content, Member member, boolean liked, Long likeCnt,
		Long commentCnt, boolean viewMySelf, LocalDateTime createdAt, MemberProfile profile) {
		this(workoutHistoryId, content, MemberDto.create(member, profile), liked, likeCnt, commentCnt, viewMySelf,
			createdAt, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
	}

	public WorkoutHistoryDto withFiles(List<WorkoutHistoryFileDto> files) {
		return new WorkoutHistoryDto(workoutHistoryId, content, member, liked, likeCnt, commentCnt, viewMySelf,
			createdAt, multipartFiles, files, completedExercises);
	}

	public WorkoutHistoryDto withCompletedExercises(List<CompletedExerciseDto> completedExercises) {
		return new WorkoutHistoryDto(workoutHistoryId, content, member, liked, likeCnt, commentCnt, viewMySelf,
			createdAt, multipartFiles, files, completedExercises);
	}

	public static WorkoutHistoryDto create(HistoryAddCommand command, MemberDto memberDto) {
		return new WorkoutHistoryDto(null, command.content(), memberDto, false, null, null, false, null,
			new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
	}

	public static WorkoutHistoryDto create(WorkoutHistory history, MemberProfile memberProfile) {
		return new WorkoutHistoryDto(
			history.getWorkoutHistoryId(),
			history.getContent(),
			MemberDto.create(history.getMember(), memberProfile),
			false,
			history.getLikeCnt(),
			history.getCommentCnt(),
			history.getViewMySelf(),
			history.getCreatedAt(),
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>()
		);
	}

	public static WorkoutHistoryDto from(WorkoutHistory history) {
		return new WorkoutHistoryDto(
			history.getWorkoutHistoryId(),
			history.getContent(),
			MemberDto.from(history.getMember()),
			false,
			history.getLikeCnt(),
			history.getCommentCnt(),
			history.getViewMySelf(),
			history.getCreatedAt(),
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>()
		);
	}
}
