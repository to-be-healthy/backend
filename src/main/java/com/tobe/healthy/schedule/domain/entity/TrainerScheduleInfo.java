package com.tobe.healthy.schedule.domain.entity;

import com.tobe.healthy.common.error.CustomException;
import com.tobe.healthy.common.error.ErrorCode;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.schedule.domain.dto.in.CommandRegisterDefaultLessonTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerScheduleInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trainer_schedule_info_id")
    private Long id;

    private LocalTime lessonStartTime;

    private LocalTime lessonEndTime;

    private LocalTime lunchStartTime;

    private LocalTime lunchEndTime;

    @Enumerated(EnumType.STRING)
    private LessonTime lessonTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Member trainer;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "trainerScheduleInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrainerScheduleClosedDaysInfo> trainerScheduleClosedDays = new ArrayList<>();

    private TrainerScheduleInfo(LocalTime lessonStartTime, LocalTime lessonEndTime,
                                LocalTime lunchStartTime, LocalTime lunchEndTime,
                                LessonTime lessonTime, Member trainer) {
        this.lessonStartTime = lessonStartTime;
        this.lessonEndTime = lessonEndTime;
        this.lunchStartTime = lunchStartTime;
        this.lunchEndTime = lunchEndTime;
        this.lessonTime = lessonTime;
        this.trainer = trainer;
    }

    public void changeDefaultLessonTime(
            CommandRegisterDefaultLessonTime request,
            List<TrainerScheduleClosedDaysInfo> closedDays
    ) {
        this.lessonStartTime = request.getLessonStartTime();
        this.lessonEndTime = request.getLessonEndTime();
        this.lunchStartTime = request.getLunchStartTime();
        this.lunchEndTime = request.getLunchEndTime();
        this.lessonTime = fromDescription(request.getLessonTime());
        this.trainerScheduleClosedDays.clear();
        this.trainerScheduleClosedDays.addAll(closedDays);
    }

    public static TrainerScheduleInfo registerDefaultLessonTime(
            CommandRegisterDefaultLessonTime request,
            Member trainer
    ) {
        return new TrainerScheduleInfo(
                request.getLessonStartTime(),
                request.getLessonEndTime(),
                request.getLunchStartTime(),
                request.getLunchEndTime(),
                LessonTime.ONE_HOUR,
                trainer
        );
    }

    public static LessonTime fromDescription(int description) {
        for (LessonTime lt : LessonTime.values()) {
            if (lt.getDescription() == description) {
                return lt;
            }
        }
        throw new CustomException(ErrorCode.INVALID_LESSON_TIME_DESCRIPTION);
    }
}
