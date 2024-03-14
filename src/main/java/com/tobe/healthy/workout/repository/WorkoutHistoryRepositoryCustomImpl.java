package com.tobe.healthy.workout.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.file.domain.dto.WorkoutHistoryFileDto;
import com.tobe.healthy.file.domain.entity.QWorkoutHistoryFile;
import com.tobe.healthy.file.domain.entity.WorkoutHistoryFile;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryDto;
import com.tobe.healthy.workout.domain.entity.QWorkoutHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class WorkoutHistoryRepositoryCustomImpl implements WorkoutHistoryRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private QWorkoutHistory qHistory = QWorkoutHistory.workoutHistory;
    private QWorkoutHistoryFile qHistoryFile = QWorkoutHistoryFile.workoutHistoryFile;

    @Override
    public Page<WorkoutHistoryDto> getWorkoutHistory(Long memberId, Pageable pageable) {
        Long totalCnt = queryFactory
                .select(qHistory.count())
                .from(qHistory)
                .where(qHistory.member.id.eq(memberId), qHistory.delYn.eq(false))
                .fetchOne();
        List<WorkoutHistoryDto> workoutHistories =  queryFactory
                .select(Projections.fields(WorkoutHistoryDto.class,
                        qHistory.workoutHistoryId,
                        qHistory.content,
                        qHistory.likeCnt
                ))
                .from(qHistory)
                .where(qHistory.member.id.eq(memberId), qHistory.delYn.eq(false))
                .orderBy(qHistory.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return PageableExecutionUtils.getPage(workoutHistories, pageable, ()-> totalCnt );
    }

    @Override
    public Page<WorkoutHistoryDto> getWorkoutHistoryByTrainer(Long trainerId, Pageable pageable) {
        Long totalCnt = queryFactory
                .select(qHistory.count())
                .from(qHistory)
                .where(qHistory.trainerId.eq(trainerId), qHistory.delYn.eq(false))
                .fetchOne();
        List<WorkoutHistoryDto> workoutHistories =  queryFactory
                .select(Projections.fields(WorkoutHistoryDto.class,
                        qHistory.workoutHistoryId,
                        qHistory.content,
                        qHistory.likeCnt
                ))
                .from(qHistory)
                .where(qHistory.trainerId.eq(trainerId), qHistory.delYn.eq(false))
                .orderBy(qHistory.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return PageableExecutionUtils.getPage(workoutHistories, pageable, ()-> totalCnt );
    }

    @Override
    public List<WorkoutHistoryFileDto> getWorkoutHistoryFile(List<Long> ids) {
        return queryFactory.select(Projections.fields(WorkoutHistoryFileDto.class,
                        qHistoryFile.id,
                        qHistoryFile.workoutHistory.workoutHistoryId,
                        qHistoryFile.fileName,
                        qHistoryFile.originalName,
                        qHistoryFile.extension,
                        qHistoryFile.filePath,
                        qHistoryFile.fileSize
                ))
                .from(qHistoryFile)
                .where(qHistoryFile.workoutHistory.workoutHistoryId.in(ids), qHistoryFile.delYn.eq(false))
                .orderBy(qHistoryFile.createdAt.desc())
                .fetch();
    }

}
