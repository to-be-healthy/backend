package com.tobe.healthy.workout.application;

import com.tobe.healthy.workout.domain.dto.ExerciseDto;
import com.tobe.healthy.workout.domain.dto.InstructionsDto;
import com.tobe.healthy.workout.domain.entity.Exercise;
import com.tobe.healthy.workout.domain.entity.ExerciseCategory;
import com.tobe.healthy.workout.domain.entity.Instructions;
import com.tobe.healthy.workout.domain.entity.PrimaryMuscle;
import com.tobe.healthy.workout.repository.ExerciseRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    public List<ExerciseDto> getExercise(ExerciseCategory category, PrimaryMuscle primaryMuscle, Pageable pageable) {
        Page<Exercise> exercises = exerciseRepository.getExercise(category, primaryMuscle, pageable);
        List<ExerciseDto> exerciseDtos = exercises.map(ExerciseDto::from).stream().toList();
        List<Long> ids = exerciseDtos.stream().map(ExerciseDto::getExerciseId).collect(Collectors.toList());
        List<Instructions> instructions = exerciseRepository.getInstructions(ids);
        return exerciseDtos.stream().map(e -> {
            List<String> instStr = instructions.stream().map(InstructionsDto::from)
                    .filter(i -> i.getExerciseId().equals(e.getExerciseId()))
                    .map(InstructionsDto::getInstructions).collect(Collectors.toList());
            e.setInstructions(instStr);
            return e;
        }).collect(Collectors.toList());
    }

}
