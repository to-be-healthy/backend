package com.tobe.healthy.workout.application;

import com.tobe.healthy.common.CustomPaging;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.workout.domain.dto.ExerciseDto;
import com.tobe.healthy.workout.domain.dto.in.CustomExerciseAddCommand;
import com.tobe.healthy.workout.domain.entity.exercise.Exercise;
import com.tobe.healthy.workout.domain.entity.exercise.ExerciseCategory;
import com.tobe.healthy.workout.repository.exercise.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.tobe.healthy.config.error.ErrorCode.*;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    public CustomPaging<ExerciseDto> getExercise(Member member, ExerciseCategory exerciseCategory, Pageable pageable, String searchValue) {
        Page<Exercise> exercises = exerciseRepository.getExercise(member.getId(), exerciseCategory, pageable, searchValue);
        List<ExerciseDto> exerciseDtos = exercises.map(ExerciseDto::from).stream().toList();

        return new CustomPaging(exerciseDtos, exercises.getPageable().getPageNumber(),
                exercises.getPageable().getPageSize(), exercises.getTotalPages(), exercises.getTotalElements(), exercises.isLast());
    }

    public void addExerciseCustom(Member member, CustomExerciseAddCommand command) {
        exerciseRepository.findByMemberIdAndCategoryAndNames(member.getId(), command.getCategory(), command.getNames())
                .ifPresent(i -> { throw new CustomException(EXERCISE_ALREADY_EXISTS); });
        exerciseRepository.save(Exercise.create(member, command));
    }

    public void deleteExerciseCustom(Member member, Long exerciseId) {
        Exercise exercise = exerciseRepository.findByExerciseIdAndMemberId(exerciseId, member.getId())
                .orElseThrow(() -> new CustomException(EXERCISE_NOT_FOUND));
        exerciseRepository.delete(exercise);
    }
}
