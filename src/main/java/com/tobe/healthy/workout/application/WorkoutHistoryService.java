package com.tobe.healthy.workout.application;

import com.tobe.healthy.common.CustomPaging;
import com.tobe.healthy.common.error.CustomException;
import com.tobe.healthy.common.redis.RedisService;
import com.tobe.healthy.member.domain.dto.MemberDto;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.workout.domain.dto.CompletedExerciseDto;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryFileDto;
import com.tobe.healthy.workout.domain.dto.in.HistoryAddCommand;
import com.tobe.healthy.workout.domain.dto.in.RegisterFile;
import com.tobe.healthy.workout.domain.dto.out.WorkoutHistoryDto;
import com.tobe.healthy.workout.domain.entity.exercise.Exercise;
import com.tobe.healthy.workout.domain.entity.workoutHistory.*;
import com.tobe.healthy.workout.repository.exercise.ExerciseRepository;
import com.tobe.healthy.workout.repository.workoutHistory.CompletedExerciseRepository;
import com.tobe.healthy.workout.repository.workoutHistory.WorkoutFileRepository;
import com.tobe.healthy.workout.repository.workoutHistory.WorkoutHistoryLikeRepository;
import com.tobe.healthy.workout.repository.workoutHistory.WorkoutHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tobe.healthy.common.Utils.S3_DOMAIN;
import static com.tobe.healthy.common.error.ErrorCode.*;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WorkoutHistoryService {

    private final FileService fileService;
    private final WorkoutHistoryLikeRepository workoutHistoryLikeRepository;
    private final WorkoutHistoryRepository workoutHistoryRepository;
    private final CompletedExerciseRepository completedExerciseRepository;
    private final MemberRepository memberRepository;
    private final WorkoutFileRepository workoutFileRepository;


    public WorkoutHistoryDto addWorkoutHistory(Member member, HistoryAddCommand command) {
        Member result = memberRepository.findByIdAndDelYnFalse(member.getId())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        WorkoutHistory history = WorkoutHistory.create(command, member, result.getGym());
        workoutHistoryRepository.save(history);
        saveCompletedExercises(history, command);
        uploadNewFiles(history, command.getFiles());
        return WorkoutHistoryDto.from(history);
    }

    private void saveCompletedExercises(WorkoutHistory history, HistoryAddCommand command) {
        List<CompletedExercise> completedExercises = command.getCompletedExercises().stream()
                .map(c -> {
                    return CompletedExercise.create(c, history, c.getNames());
                }).collect(Collectors.toList());
        completedExerciseRepository.saveAll(completedExercises);
    }

    public CustomPaging getWorkoutHistory(Member loginMember, Long memberId, Pageable pageable, String searchDate) {
        Member member = memberRepository.findByIdAndDelYnFalse(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        Page<WorkoutHistoryDto> pageDtos = workoutHistoryRepository.getWorkoutHistoryOfMonth(loginMember.getId(), memberId, pageable, searchDate);
        List<WorkoutHistoryDto> historiesDto = pageDtos.stream().toList();
        List<Long> ids = historiesDto.stream().map(WorkoutHistoryDto::getWorkoutHistoryId).collect(Collectors.toList());
        historiesDto = setHistoryListFile(historiesDto, ids);
        List<WorkoutHistoryDto> content = setHistoryListExercise(historiesDto, ids);
        CustomPaging customPaging = new CustomPaging<>(content, pageDtos.getPageable().getPageNumber(),
                pageDtos.getPageable().getPageSize(), pageDtos.getTotalPages(), pageDtos.getTotalElements(), pageDtos.isLast());
        customPaging.setMainData(MemberDto.from(member));
        return customPaging;
    }

    public WorkoutHistoryDto getWorkoutHistoryDetail(Member loginMember, Long workoutHistoryId) {
        WorkoutHistoryDto historyDto = workoutHistoryRepository.findByWorkoutHistoryId(loginMember.getId(), workoutHistoryId);
        List<Long> ids = List.of(workoutHistoryId);
        setHistoryFile(historyDto, ids);
        setHistoryExercise(historyDto, ids);
        return historyDto;
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

        history.changeViewMySelf(command.isViewMySelf());
        history.changeContent(command.getContent());
        updateCompletedExercise(command, history);
        deleteOldFiles(history, command);
        uploadNewFiles(history, command.getFiles());

        WorkoutHistoryDto historyDto = WorkoutHistoryDto.from(history);
        setHistoryFile(historyDto, List.of(history.getWorkoutHistoryId()));
        return historyDto;
    }

    private void deleteOldFiles(WorkoutHistory history, HistoryAddCommand command) {
        Set<String> oldFileNames = history.getHistoryFiles().stream()
                .map(WorkoutHistoryFiles::getFileName).collect(Collectors.toSet());
        Set<String> requestFileNames = command.getFiles().stream()
                .map(f -> getFileName(f.getFileUrl())).collect(Collectors.toSet());
        oldFileNames.removeAll(requestFileNames);
        Set<String> deleteFileNames = history.getHistoryFiles().stream()
                .map(WorkoutHistoryFiles::getFileName)
                .filter(oldFileNames::contains)
                .collect(Collectors.toSet());

        history.getHistoryFiles().stream()
            .filter(f -> deleteFileNames.contains(f.getFileName()))
            .forEach(f -> {
                fileService.deleteHistoryFile(f.getFileName());
                f.deleteWorkoutHistoryFile();
            });
    }

    private void updateCompletedExercise(HistoryAddCommand command, WorkoutHistory history) {
        completedExerciseRepository.deleteAllInBatch(history.getCompletedExercises());
        completedExerciseRepository.flush();
        saveCompletedExercises(history, command);
    }

    public void likeWorkoutHistory(Member member, Long workoutHistoryId) {
        WorkoutHistory history = workoutHistoryRepository.findByWorkoutHistoryIdAndDelYnFalse(workoutHistoryId)
                .orElseThrow(() -> new CustomException(WORKOUT_HISTORY_NOT_FOUND));
        WorkoutHistoryLikePK likePk = WorkoutHistoryLikePK.create(history, member);
        workoutHistoryLikeRepository.findById(likePk).ifPresent(i -> {
            throw new CustomException(LIKE_ALREADY_EXISTS);
        });
        workoutHistoryLikeRepository.save(WorkoutHistoryLike.from(likePk));
        history.changeLikeCnt(workoutHistoryLikeRepository.getLikeCnt(history.getWorkoutHistoryId()));
    }

    public void deleteLikeWorkoutHistory(Member member, Long workoutHistoryId) {
        WorkoutHistory history = workoutHistoryRepository.findByWorkoutHistoryIdAndDelYnFalse(workoutHistoryId)
                .orElseThrow(() -> new CustomException(WORKOUT_HISTORY_NOT_FOUND));
        workoutHistoryLikeRepository.delete(WorkoutHistoryLike.from(WorkoutHistoryLikePK.create(history, member)));
        history.changeLikeCnt(workoutHistoryLikeRepository.getLikeCnt(history.getWorkoutHistoryId()));
    }

    private void setHistoryFile(WorkoutHistoryDto historyDto, List<Long> ids) {
        List<WorkoutHistoryFiles> files = workoutHistoryRepository.getWorkoutHistoryFile(ids);
        List<WorkoutHistoryFileDto> filesDto = files.stream().map(WorkoutHistoryFileDto::from).collect(Collectors.toList());
        historyDto.setFiles(filesDto);
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

    private void setHistoryExercise(WorkoutHistoryDto historyDto, List<Long> ids) {
        List<CompletedExercise> exercises = completedExerciseRepository.getCompletedExercise(ids);
        List<CompletedExerciseDto> exerciseDtos = exercises.stream().map(CompletedExerciseDto::from).collect(Collectors.toList());
        historyDto.setCompletedExercises(exerciseDtos);
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

    private void uploadNewFiles(WorkoutHistory history, List<RegisterFile> files) {
        for (int i = 0; i < files.size(); i++) {
            RegisterFile fileInfo = files.get(i);
            if(fileInfo.getFileUrl().startsWith(S3_DOMAIN)){
                fileInfo.setFileOrder(i+1);
                String oldSavedFileName = fileInfo.getFileUrl().replace(S3_DOMAIN, "");
                RegisterFile result = fileService.moveDirTempToOrigin("workout-history/", oldSavedFileName);
                workoutFileRepository.save(WorkoutHistoryFiles.create(history, result.getFileUrl(), fileInfo.getFileOrder()));
            }
        }
    }

    public CustomPaging<WorkoutHistoryDto> getWorkoutHistoryOnCommunity(Long memberId, Member loginMember, Pageable pageable, String searchDate) {
        if(ObjectUtils.isEmpty(memberId)){ //커뮤니티 목록 조회
            return getCommunityList(loginMember, pageable, searchDate);
        }else{ //커뮤니티 학생 한명의 목록 조회
            return getCommunityListByMember(memberId, loginMember, pageable, searchDate);
        }
    }

    private CustomPaging<WorkoutHistoryDto> getCommunityList(Member loginMember, Pageable pageable, String searchDate) {
        Member member = memberRepository.findByIdAndDelYnFalse(loginMember.getId())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        Page<WorkoutHistoryDto> pageDtos = workoutHistoryRepository.getWorkoutHistoryOnCommunity(loginMember.getId(), member.getGym().getId(), pageable, searchDate);
        List<WorkoutHistoryDto> historiesDto = pageDtos.stream().toList();
        List<Long> ids = historiesDto.stream().map(WorkoutHistoryDto::getWorkoutHistoryId).collect(Collectors.toList());
        historiesDto = setHistoryListFile(historiesDto, ids);
        List<WorkoutHistoryDto> content = setHistoryListExercise(historiesDto, ids);
        return new CustomPaging<>(content, pageDtos.getPageable().getPageNumber(),
                pageDtos.getPageable().getPageSize(), pageDtos.getTotalPages(), pageDtos.getTotalElements(), pageDtos.isLast());
    }

    private CustomPaging<WorkoutHistoryDto> getCommunityListByMember(Long memberId, Member loginMember, Pageable pageable, String searchDate) {
        Member member = memberRepository.findByIdAndDelYnFalse(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        Page<WorkoutHistoryDto> pageDtos = workoutHistoryRepository.getWorkoutHistoryOnCommunityByMember(loginMember.getId(), loginMember.getGym().getId(), memberId, pageable, searchDate);
        List<WorkoutHistoryDto> historiesDto = pageDtos.stream().toList();
        List<Long> ids = historiesDto.stream().map(WorkoutHistoryDto::getWorkoutHistoryId).collect(Collectors.toList());
        historiesDto = setHistoryListFile(historiesDto, ids);
        List<WorkoutHistoryDto> content = setHistoryListExercise(historiesDto, ids);

        CustomPaging customPaging =  new CustomPaging<>(content, pageDtos.getPageable().getPageNumber(),
                pageDtos.getPageable().getPageSize(), pageDtos.getTotalPages(), pageDtos.getTotalElements(), pageDtos.isLast());
        customPaging.setMainData(MemberDto.from(member));
        return customPaging;
    }

}
