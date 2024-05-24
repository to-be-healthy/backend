package com.tobe.healthy.workout.application;

import com.tobe.healthy.common.CustomPaging;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.workout.domain.dto.ExerciseDto;
import com.tobe.healthy.workout.domain.dto.in.CustomExerciseAddCommand;
import com.tobe.healthy.workout.domain.entity.Exercise;
import com.tobe.healthy.workout.domain.entity.ExerciseCategory;
import com.tobe.healthy.workout.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    public CustomPaging<ExerciseDto> getExercise(Member member, ExerciseCategory exerciseCategory, Pageable pageable) {
        Page<Exercise> exercises = exerciseRepository.getExercise(exerciseCategory, pageable);
        List<ExerciseDto> content = exercises.map(ExerciseDto::from).stream().toList();
        return new CustomPaging(content, exercises.getPageable().getPageNumber(),
                exercises.getPageable().getPageSize(), exercises.getTotalPages(), exercises.getTotalElements(), exercises.isLast());
    }

}
