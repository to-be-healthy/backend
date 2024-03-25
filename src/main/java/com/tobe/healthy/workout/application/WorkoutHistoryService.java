package com.tobe.healthy.workout.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.file.application.FileService;
import com.tobe.healthy.file.domain.dto.WorkoutHistoryFileDto;
import com.tobe.healthy.file.domain.entity.WorkoutHistoryFile;
import com.tobe.healthy.member.domain.dto.MemberDto;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
import com.tobe.healthy.workout.domain.dto.CompletedExerciseDto;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryDto;
import com.tobe.healthy.workout.domain.dto.in.HistoryAddCommand;
import com.tobe.healthy.workout.domain.entity.*;
import com.tobe.healthy.workout.repository.CompletedExerciseRepository;
import com.tobe.healthy.workout.repository.ExerciseRepository;
import com.tobe.healthy.workout.repository.WorkoutHistoryLikeRepository;
import com.tobe.healthy.workout.repository.WorkoutHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tobe.healthy.config.error.ErrorCode.EXERCISE_NOT_FOUND;
import static com.tobe.healthy.config.error.ErrorCode.WORKOUT_HISTORY_NOT_FOUND;
import static java.io.File.separator;


@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutHistoryService {

    private final FileService fileService;
    private final WorkoutHistoryLikeRepository workoutHistoryLikeRepository;
    private final WorkoutHistoryRepository workoutHistoryRepository;
    private final TrainerMemberMappingRepository mappingRepository;
    private final CompletedExerciseRepository completedExerciseRepository;
    private final ExerciseRepository exerciseRepository;


    @Transactional
    public WorkoutHistoryDto addWorkoutHistory(Member member, HistoryAddCommand command) {
        Optional<TrainerMemberMapping> mapping = mappingRepository.findTop1ByMemberIdOrderByCreatedAtDesc(member.getId());
        Long trainerId = mapping.map(TrainerMemberMapping::getTrainerId).orElse(null);
        MemberDto memberDto = MemberDto.from(member);
        WorkoutHistoryDto workoutHistoryDto = WorkoutHistoryDto.create(command, memberDto, trainerId);
        WorkoutHistory history = WorkoutHistory.create(workoutHistoryDto, member);
        workoutHistoryRepository.save(history);
        saveCompletedExercises(history, command);
        fileService.uploadWorkoutFiles(history, command.getFiles());
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

    public List<WorkoutHistoryDto> getWorkoutHistory(Long memberId, Pageable pageable) {
        Page<WorkoutHistory> histories = workoutHistoryRepository.getWorkoutHistory(memberId, pageable);
        List<WorkoutHistoryDto> historiesDto = histories.map(WorkoutHistoryDto::from).stream().toList();
        List<Long> ids = historiesDto.stream().map(WorkoutHistoryDto::getWorkoutHistoryId).collect(Collectors.toList());
        historiesDto = setHistoryListFile(historiesDto, ids);
        List<WorkoutHistoryDto> result = setHistoryListExercise(historiesDto, ids);
        return result.isEmpty() ? null : result;
    }

    public List<WorkoutHistoryDto> getWorkoutHistoryByTrainer(Long trainerId, Pageable pageable) {
        Page<WorkoutHistory> histories = workoutHistoryRepository.getWorkoutHistoryByTrainer(trainerId, pageable);
        List<WorkoutHistoryDto> historiesDto = histories.map(WorkoutHistoryDto::from).stream().toList();
        List<Long> ids = historiesDto.stream().map(WorkoutHistoryDto::getWorkoutHistoryId).collect(Collectors.toList());
        historiesDto = setHistoryListFile(historiesDto, ids);
        List<WorkoutHistoryDto> result = setHistoryListExercise(historiesDto, ids);
        return result.isEmpty() ? null : result;
    }

    public WorkoutHistoryDto getWorkoutHistoryDetail(Long workoutHistoryId) {
        WorkoutHistory history = workoutHistoryRepository.findByWorkoutHistoryIdAndDelYnFalse(workoutHistoryId)
                .orElseThrow(() -> new CustomException(WORKOUT_HISTORY_NOT_FOUND));
        List<Long> ids = List.of(history.getWorkoutHistoryId());
        WorkoutHistoryDto historyDto = setHistoryFile(WorkoutHistoryDto.from(history), ids);
        return setHistoryExercise(historyDto, ids);
    }

    @Transactional
    public void deleteWorkoutHistory(Member member, Long workoutHistoryId) {
        WorkoutHistory history = workoutHistoryRepository.findByWorkoutHistoryIdAndMemberIdAndDelYnFalse(workoutHistoryId, member.getId())
                .orElseThrow(() -> new CustomException(WORKOUT_HISTORY_NOT_FOUND));
        history.deleteWorkoutHistory();
        completedExerciseRepository.deleteAllInBatch(history.getCompletedExercises());
        workoutHistoryLikeRepository.deleteLikeByWorkoutHistoryId(workoutHistoryId);
        history.getHistoryFiles().forEach(file ->
            fileService.deleteFile(file.getFilePath() + separator + file.getFileName() + file.getExtension())
        );
    }

    @Transactional
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
        history.getHistoryFiles().forEach(file ->
                fileService.deleteFile(file.getFilePath() + separator + file.getFileName() + file.getExtension())
        );
        fileService.uploadWorkoutFiles(history, command.getFiles());
        return setHistoryFile(WorkoutHistoryDto.from(history), List.of(history.getWorkoutHistoryId()));
    }

    @Transactional
    public void likeWorkoutHistory(Member member, Long workoutHistoryId) {
        WorkoutHistory history = workoutHistoryRepository.findByWorkoutHistoryIdAndDelYnFalse(workoutHistoryId)
                .orElseThrow(() -> new CustomException(WORKOUT_HISTORY_NOT_FOUND));
        workoutHistoryLikeRepository.save(WorkoutHistoryLike.from(WorkoutHistoryLikePK.create(history, member)));
        history.updateLikeCnt(workoutHistoryLikeRepository.getLikeCnt(history.getWorkoutHistoryId()));
    }

    @Transactional
    public void deleteLikeWorkoutHistory(Member member, Long workoutHistoryId) {
        WorkoutHistory history = workoutHistoryRepository.findByWorkoutHistoryIdAndDelYnFalse(workoutHistoryId)
                .orElseThrow(() -> new CustomException(WORKOUT_HISTORY_NOT_FOUND));
        workoutHistoryLikeRepository.delete(WorkoutHistoryLike.from(WorkoutHistoryLikePK.create(history, member)));
        history.updateLikeCnt(workoutHistoryLikeRepository.getLikeCnt(history.getWorkoutHistoryId()));
    }

    private WorkoutHistoryDto setHistoryFile(WorkoutHistoryDto historyDto, List<Long> ids) {
        List<WorkoutHistoryFile> files = workoutHistoryRepository.getWorkoutHistoryFile(ids);
        List<WorkoutHistoryFileDto> filesDto = files.stream().map(WorkoutHistoryFileDto::from).collect(Collectors.toList());
        historyDto.setFiles(filesDto);
        return historyDto;
    }

    private List<WorkoutHistoryDto> setHistoryListFile(List<WorkoutHistoryDto> historiesDto, List<Long> ids) {
        List<WorkoutHistoryFile> files = workoutHistoryRepository.getWorkoutHistoryFile(ids);
        return historiesDto.stream().map(h -> {
            List<WorkoutHistoryFileDto> thisFiles = files.stream().map(WorkoutHistoryFileDto::from)
                    .filter(f -> f.getWorkoutHistoryId() == h.getWorkoutHistoryId()).collect(Collectors.toList());
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
                    .filter(e -> e.getWorkoutHistoryId() == h.getWorkoutHistoryId()).collect(Collectors.toList());
            h.setCompletedExercises(exerciseDtos);
            return h;
        }).collect(Collectors.toList());
    }

}
