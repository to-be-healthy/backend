package com.tobe.healthy.workout.application;

import com.tobe.healthy.common.CustomPaging;
import com.tobe.healthy.common.redis.RedisService;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.file.FileService;
import com.tobe.healthy.file.RegisterFile;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.workout.domain.dto.CompletedExerciseDto;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryFileDto;
import com.tobe.healthy.workout.domain.dto.in.HistoryAddCommand;
import com.tobe.healthy.workout.domain.dto.out.WorkoutHistoryDto;
import com.tobe.healthy.workout.domain.entity.*;
import com.tobe.healthy.workout.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.tobe.healthy.common.redis.RedisKeyPrefix.TEMP_FILE_URI;
import static com.tobe.healthy.config.error.ErrorCode.*;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WorkoutHistoryService {

    private final FileService fileService;
    private final WorkoutHistoryLikeRepository workoutHistoryLikeRepository;
    private final WorkoutHistoryRepository workoutHistoryRepository;
    private final CompletedExerciseRepository completedExerciseRepository;
    private final ExerciseRepository exerciseRepository;
    private final MemberRepository memberRepository;
    private final WorkoutFileRepository workoutFileRepository;
    private final RedisService redisService;


    public WorkoutHistoryDto addWorkoutHistory(Member member, HistoryAddCommand command) {
        Member result = memberRepository.findByMemberIdWithGym(member.getId());
        WorkoutHistory history = WorkoutHistory.create(command, member, result.getGym());
        workoutHistoryRepository.save(history);
        saveCompletedExercises(history, command);
        uploadWorkoutFiles(history, command.getFiles());
        return WorkoutHistoryDto.from(history);
    }

    private void saveCompletedExercises(WorkoutHistory history, HistoryAddCommand command) {
        List<CompletedExercise> completedExercises = command.getCompletedExercises().stream()
                .map(c -> {
                    Exercise exercise = exerciseRepository.findById(c.getExerciseId()).orElseThrow(() -> new CustomException(EXERCISE_NOT_FOUND));
                    return CompletedExercise.create(c, history, exercise.getNames());
                }).collect(Collectors.toList());
        completedExerciseRepository.saveAll(completedExercises);
    }

    public CustomPaging<WorkoutHistoryDto> getWorkoutHistory(Member loginMember, Long memberId, Pageable pageable, String searchDate) {
        Page<WorkoutHistoryDto> pageDtos = workoutHistoryRepository.getWorkoutHistoryOfMonth(loginMember.getId(), memberId, pageable, searchDate);
        List<WorkoutHistoryDto> historiesDto = pageDtos.stream().toList();
        List<Long> ids = historiesDto.stream().map(WorkoutHistoryDto::getWorkoutHistoryId).collect(Collectors.toList());
        historiesDto = setHistoryListFile(historiesDto, ids);
        List<WorkoutHistoryDto> content = setHistoryListExercise(historiesDto, ids);
        return new CustomPaging(content, pageDtos.getPageable().getPageNumber(),
                pageDtos.getPageable().getPageSize(), pageDtos.getTotalPages(), pageDtos.getTotalElements(), pageDtos.isLast());
    }

    public WorkoutHistoryDto getWorkoutHistoryDetail(Long workoutHistoryId) {
        WorkoutHistory history = workoutHistoryRepository.findByWorkoutHistoryIdAndDelYnFalse(workoutHistoryId)
                .orElseThrow(() -> new CustomException(WORKOUT_HISTORY_NOT_FOUND));
        List<Long> ids = List.of(workoutHistoryId);
        WorkoutHistoryDto historyDto = setHistoryFile(WorkoutHistoryDto.from(history), ids);
        return setHistoryExercise(historyDto, ids);
    }

    public void deleteWorkoutHistory(Member member, Long workoutHistoryId) {
        WorkoutHistory history = workoutHistoryRepository.findByWorkoutHistoryIdAndMemberIdAndDelYnFalse(workoutHistoryId, member.getId())
                .orElseThrow(() -> new CustomException(WORKOUT_HISTORY_NOT_FOUND));
        history.deleteWorkoutHistory();
        completedExerciseRepository.deleteAllInBatch(history.getCompletedExercises());
        workoutHistoryLikeRepository.deleteLikeByWorkoutHistoryId(workoutHistoryId);
        history.getHistoryFiles().forEach(file -> fileService.deleteHistoryFile(getFileName(file.getFileUrl())));
    }

    public WorkoutHistoryDto updateWorkoutHistory(Member member, Long workoutHistoryId, HistoryAddCommand command) {
        WorkoutHistory history = workoutHistoryRepository.findByWorkoutHistoryIdAndMemberIdAndDelYnFalse(workoutHistoryId, member.getId())
                .orElseThrow(() -> new CustomException(WORKOUT_HISTORY_NOT_FOUND));
        //내용 수정
        history.updateContent(command.getContent());
        //운동종류 수정
        completedExerciseRepository.deleteAllInBatch(history.getCompletedExercises());
        completedExerciseRepository.flush();
        saveCompletedExercises(history, command);
        //파일 수정
        history.deleteFiles();
        history.getHistoryFiles().forEach(file -> fileService.deleteHistoryFile(getFileName(file.getFileUrl())));
        uploadWorkoutFiles(history, command.getFiles());
        return setHistoryFile(WorkoutHistoryDto.from(history), List.of(history.getWorkoutHistoryId()));
    }

    public void likeWorkoutHistory(Member member, Long workoutHistoryId) {
        WorkoutHistory history = workoutHistoryRepository.findByWorkoutHistoryIdAndDelYnFalse(workoutHistoryId)
                .orElseThrow(() -> new CustomException(WORKOUT_HISTORY_NOT_FOUND));
        WorkoutHistoryLikePK likePk = WorkoutHistoryLikePK.create(history, member);
        workoutHistoryLikeRepository.findById(likePk).ifPresent(i -> {
            throw new CustomException(LIKE_ALREADY_EXISTS);
        });
        workoutHistoryLikeRepository.save(WorkoutHistoryLike.from(likePk));
        history.updateLikeCnt(workoutHistoryLikeRepository.getLikeCnt(history.getWorkoutHistoryId()));
    }

    public void deleteLikeWorkoutHistory(Member member, Long workoutHistoryId) {
        WorkoutHistory history = workoutHistoryRepository.findByWorkoutHistoryIdAndDelYnFalse(workoutHistoryId)
                .orElseThrow(() -> new CustomException(WORKOUT_HISTORY_NOT_FOUND));
        workoutHistoryLikeRepository.delete(WorkoutHistoryLike.from(WorkoutHistoryLikePK.create(history, member)));
        history.updateLikeCnt(workoutHistoryLikeRepository.getLikeCnt(history.getWorkoutHistoryId()));
    }

    private WorkoutHistoryDto setHistoryFile(WorkoutHistoryDto historyDto, List<Long> ids) {
        List<WorkoutHistoryFiles> files = workoutHistoryRepository.getWorkoutHistoryFile(ids);
        List<WorkoutHistoryFileDto> filesDto = files.stream().map(WorkoutHistoryFileDto::from).collect(Collectors.toList());
        historyDto.setFiles(filesDto);
        return historyDto;
    }

    private List<WorkoutHistoryDto> setHistoryListFile(List<WorkoutHistoryDto> historiesDto, List<Long> ids) {
        List<WorkoutHistoryFiles> files = workoutHistoryRepository.getWorkoutHistoryFile(ids);
        return historiesDto.stream().map(h -> {
            List<WorkoutHistoryFileDto> thisFiles = files.stream().map(WorkoutHistoryFileDto::from)
                    .filter(f -> f.getWorkoutHistoryId().equals(h.getWorkoutHistoryId())).collect(Collectors.toList());
            h.setFiles(thisFiles);
            return h;
        }).collect(Collectors.toList());
    }

    private WorkoutHistoryDto setHistoryExercise(WorkoutHistoryDto historyDto, List<Long> ids) {
        List<CompletedExercise> exercises = completedExerciseRepository.getCompletedExercise(ids);
        List<CompletedExerciseDto> exerciseDtos = exercises.stream().map(CompletedExerciseDto::from).collect(Collectors.toList());
        historyDto.setCompletedExercises(exerciseDtos);
        return historyDto;
    }

    private List<WorkoutHistoryDto> setHistoryListExercise(List<WorkoutHistoryDto> historiesDto, List<Long> ids) {
        List<CompletedExercise> exercises = completedExerciseRepository.getCompletedExercise(ids);
        return historiesDto.stream().map(h -> {
            List<CompletedExerciseDto> exerciseDtos = exercises.stream().map(CompletedExerciseDto::from)
                    .filter(e -> e.getWorkoutHistoryId().equals(h.getWorkoutHistoryId())).collect(Collectors.toList());
            h.setCompletedExercises(exerciseDtos);
            return h;
        }).collect(Collectors.toList());
    }

    private String getFileName(String url) {
        String[] arr = url.split("/");
        return arr[arr.length - 1];
    }

    private void uploadWorkoutFiles(WorkoutHistory history, List<RegisterFile> files) {
        for (RegisterFile fileInfo : files) {
            workoutFileRepository.save(WorkoutHistoryFiles.create(history, fileInfo.getFileUrl(), fileInfo.getFileOrder()));
            redisService.deleteValues(TEMP_FILE_URI.getDescription() + fileInfo.getFileUrl());
        }
    }

    public CustomPaging<WorkoutHistoryDto> getWorkoutHistoryMyGym(Long studentId, Pageable pageable, String searchDate) {
        Member member = memberRepository.findByMemberIdWithGym(studentId);
        Page<WorkoutHistory> pageDtos = workoutHistoryRepository.getWorkoutHistoryByGym(member.getGym().getId(), pageable, searchDate);
        List<WorkoutHistoryDto> historiesDto = pageDtos.map(WorkoutHistoryDto::from).stream().toList();
        List<Long> ids = historiesDto.stream().map(WorkoutHistoryDto::getWorkoutHistoryId).collect(Collectors.toList());
        historiesDto = setHistoryListFile(historiesDto, ids);
        List<WorkoutHistoryDto> content = setHistoryListExercise(historiesDto, ids);
        return new CustomPaging(content, pageDtos.getPageable().getPageNumber(),
                pageDtos.getPageable().getPageSize(), pageDtos.getTotalPages(), pageDtos.getTotalElements(), pageDtos.isLast());
    }

}
