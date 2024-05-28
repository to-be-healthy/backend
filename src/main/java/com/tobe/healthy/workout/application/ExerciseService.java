package com.tobe.healthy.workout.application;

import com.tobe.healthy.common.CustomPaging;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.workout.domain.dto.ExerciseDto;
import com.tobe.healthy.workout.domain.dto.in.CustomExerciseAddCommand;
import com.tobe.healthy.workout.domain.entity.exercise.Exercise;
import com.tobe.healthy.workout.domain.entity.exercise.ExerciseCategory;
import com.tobe.healthy.workout.domain.entity.exercise.ExerciseCustom;
import com.tobe.healthy.workout.repository.exercise.ExerciseCustomRepository;
import com.tobe.healthy.workout.repository.exercise.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.tobe.healthy.config.error.ErrorCode.EXERCISE_NOT_FOUND;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseCustomRepository exerciseCustomRepository;

    public CustomPaging<ExerciseDto> getExercise(Member member, ExerciseCategory exerciseCategory, Pageable pageable) {
        List<ExerciseCustom> customExercises = exerciseCustomRepository.findByMemberIdAndCategory(member.getId(), exerciseCategory);
        List<ExerciseDto> customExercisesDtos = customExercises.stream().map(ExerciseDto::from).toList();
        //TODO: 커스텀 운동 어떻게 내려줄지 response 의논 후 수정하기

        Page<Exercise> exercises = exerciseRepository.getExercise(exerciseCategory, pageable);
        List<ExerciseDto> exerciseDtos = exercises.map(ExerciseDto::from).stream().toList();

        return new CustomPaging(exerciseDtos, exercises.getPageable().getPageNumber(),
                exercises.getPageable().getPageSize(), exercises.getTotalPages(), exercises.getTotalElements(), exercises.isLast());
    }

    public void addExerciseCustom(Member member, CustomExerciseAddCommand command) {
        exerciseCustomRepository.save(ExerciseCustom.create(member, command));
    }

    public void deleteExerciseCustom(Member member, Long exerciseCustomId) {
        ExerciseCustom exerciseCustom = exerciseCustomRepository.findByExerciseCustomIdAndMemberId(exerciseCustomId, member.getId())
                .orElseThrow(() -> new CustomException(EXERCISE_NOT_FOUND));
        exerciseCustomRepository.delete(exerciseCustom);
    }
}
