package com.tobe.healthy.workout.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.config.error.ErrorCode;
import com.tobe.healthy.file.application.FileService;
import com.tobe.healthy.file.domain.dto.WorkoutHistoryFileDto;
import com.tobe.healthy.file.repository.WorkoutFileRepository;
import com.tobe.healthy.member.domain.dto.MemberDto;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryDto;
import com.tobe.healthy.workout.domain.dto.in.WorkoutHistoryAddCommand;
import com.tobe.healthy.workout.domain.dto.out.WorkoutHistoryCommandResult;
import com.tobe.healthy.workout.domain.entity.WorkoutHistory;
import com.tobe.healthy.workout.repository.WorkoutHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tobe.healthy.config.error.ErrorCode.WORKOUT_HISTORY_NOT_FOUND;
import static java.io.File.separator;
import static java.nio.file.Paths.get;
import static org.springframework.util.StringUtils.cleanPath;


@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutService {

    private final FileService fileService;
    private final WorkoutFileRepository workoutFileRepository;
    private final WorkoutHistoryRepository workoutHistoryRepository;
    private final TrainerMemberMappingRepository mappingRepository;


    @Transactional
    public WorkoutHistoryCommandResult addWorkoutHistory(Member member, WorkoutHistoryAddCommand command) {
        Optional<TrainerMemberMapping> mapping = mappingRepository.findTop1ByMemberIdOrderByCreatedAtDesc(member.getId());
        Long trainerId = mapping.map(TrainerMemberMapping::getTrainerId).orElse(null);
        MemberDto memberDto = MemberDto.from(member);
        WorkoutHistoryDto workoutHistoryDto = WorkoutHistoryDto.create(command.getContent(), memberDto, command.getFiles(), trainerId);
        WorkoutHistory history = WorkoutHistory.create(workoutHistoryDto, member);
        workoutHistoryRepository.save(history);
        history = workoutHistoryRepository.findById(history.getWorkoutHistoryId())
            .orElseThrow(() -> new CustomException(ErrorCode.WORKOUT_HISTORY_NOT_FOUND));
        fileService.uploadWorkoutFiles(history, command.getFiles());
        return new WorkoutHistoryCommandResult(history.getWorkoutHistoryId(),
                history.getMember().getId(),
                history.getContent());
    }

    public List<WorkoutHistoryDto> getWorkoutHistory(Long memberId, Pageable pageable) {
        Page<WorkoutHistoryDto> histories = workoutHistoryRepository.getWorkoutHistory(memberId, pageable);
        List<Long> ids = histories.stream().map(WorkoutHistoryDto::getWorkoutHistoryId).collect(Collectors.toList());
        List<WorkoutHistoryFileDto> files = workoutHistoryRepository.getWorkoutHistoryFile(ids);
        return histories.stream().map(h -> {
            List<WorkoutHistoryFileDto> thisFiles = files.stream()
                    .filter(f -> f.getWorkoutHistoryId() == h.getWorkoutHistoryId()).collect(Collectors.toList());
            h.setFiles(thisFiles);
            return h;
        }).collect(Collectors.toList());
    }

    public List<WorkoutHistoryDto> getWorkoutHistoryByTrainer(Long trainerId, Pageable pageable) {
        Page<WorkoutHistoryDto> histories = workoutHistoryRepository.getWorkoutHistoryByTrainer(trainerId, pageable);
        List<Long> ids = histories.stream().map(WorkoutHistoryDto::getWorkoutHistoryId).collect(Collectors.toList());
        List<WorkoutHistoryFileDto> files = workoutHistoryRepository.getWorkoutHistoryFile(ids);
        return histories.stream().map(h -> {
            List<WorkoutHistoryFileDto> thisFiles = files.stream()
                    .filter(f -> f.getWorkoutHistoryId() == h.getWorkoutHistoryId()).collect(Collectors.toList());
            h.setFiles(thisFiles);
            return h;
        }).collect(Collectors.toList());
    }

    public WorkoutHistoryDto getWorkoutHistoryDetail(Long workoutHistoryId) {
        WorkoutHistory history = workoutHistoryRepository.findById(workoutHistoryId)
                .orElseThrow(() -> new CustomException(WORKOUT_HISTORY_NOT_FOUND));
        List<Long> ids = Arrays.asList(history.getWorkoutHistoryId());
        List<WorkoutHistoryFileDto> files = workoutHistoryRepository.getWorkoutHistoryFile(ids);
        return WorkoutHistoryDto.create(history, files);
    }

    @Transactional
    public void deleteWorkoutHistory(Long workoutHistoryId) {
        WorkoutHistory history = workoutHistoryRepository.findById(workoutHistoryId)
                .orElseThrow(() -> new CustomException(WORKOUT_HISTORY_NOT_FOUND));
        workoutHistoryRepository.delete(history);
        history.getHistoryFiles().forEach(file ->
                fileService.deleteFile(file.getFilePath() + separator + file.getFileName() + file.getExtension()));
    }

    @Transactional
    public WorkoutHistoryCommandResult updateWorkoutHistory(Member member, Long workoutHistoryId, WorkoutHistoryAddCommand command) {
        WorkoutHistory history = workoutHistoryRepository.findByWorkoutHistoryIdAndMemberId(workoutHistoryId, member.getId())
            .orElseThrow(() -> new CustomException(WORKOUT_HISTORY_NOT_FOUND));
        history.updateContent(command.getContent());
        workoutFileRepository.deleteAllInBatch(history.getHistoryFiles());
        workoutFileRepository.flush();
        history.getHistoryFiles().forEach(file ->
                fileService.deleteFile(file.getFilePath() + separator + file.getFileName() + file.getExtension()));
        fileService.uploadWorkoutFiles(history, command.getFiles());
        return new WorkoutHistoryCommandResult(history.getWorkoutHistoryId(),
                history.getMember().getId(),
                history.getContent());
    }
}
