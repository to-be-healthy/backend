package com.tobe.healthy.workout.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.config.error.ErrorCode;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.workout.domain.dto.in.WorkoutHistoryAddCommand;
import com.tobe.healthy.workout.domain.dto.out.WorkoutHistoryAddCommandResult;
import com.tobe.healthy.workout.domain.entity.WorkoutHistory;
import com.tobe.healthy.workout.repository.WorkoutHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutService {

    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final WorkoutHistoryRepository workoutHistoryRepository;

    @Transactional
    public WorkoutHistoryAddCommandResult addWorkoutHistory(WorkoutHistoryAddCommand request) {
        Optional<Member> member = memberRepository.findById(request.getMemberId());
        member.orElseThrow(()-> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        WorkoutHistory history = WorkoutHistory.create(request.getContent(), member.get());
        workoutHistoryRepository.save(history);
        history = workoutHistoryRepository.findById(history.getWorkoutHistoryId())
            .orElseThrow(() -> new CustomException(ErrorCode.WORKOUT_HISTORY_NOT_FOUND));
        return new WorkoutHistoryAddCommandResult(history.getWorkoutHistoryId(),
                history.getMember().getId(),
                history.getContent());
    }

}
