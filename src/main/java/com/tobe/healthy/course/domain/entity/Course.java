package com.tobe.healthy.course.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.course.domain.dto.in.CourseUpdateCommand;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;


@Entity
@Table(name = "course")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class Course extends BaseTimeEntity<Course, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "trainer_id")
    @ToString.Exclude
    private Member trainer;

    private int totalLessonCnt;
    private int remainLessonCnt;

    @OneToMany(fetch = LAZY, mappedBy = "course", cascade = CascadeType.ALL)
    @ToString.Exclude
    private final List<CourseHistory> courseHistories = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "course", cascade = CascadeType.PERSIST)
    @ToString.Exclude
    private final List<Schedule> schedules = new ArrayList<>();

    @Builder
    public Course(Member member, Member trainer, int totalLessonCnt, int remainLessonCnt) {
        this.member = member;
        this.trainer = trainer;
        this.totalLessonCnt = totalLessonCnt;
        this.remainLessonCnt = remainLessonCnt;
    }

    public static Course create(Member member, Member trainer, int totalLessonCnt, int remainLessonCnt) {
        return Course.builder()
                .member(member)
                .trainer(trainer)
                .totalLessonCnt(totalLessonCnt)
                .remainLessonCnt(remainLessonCnt)
                .build();
    }

    public void updateTotalLessonCnt(CourseUpdateCommand command){
        this.totalLessonCnt = command.getCalculation().apply(totalLessonCnt, command.getUpdateCnt());
    }

    public void updateRemainLessonCnt(CourseUpdateCommand command){
        this.remainLessonCnt = command.getCalculation().apply(remainLessonCnt, command.getUpdateCnt());
    }

    public void deleteSchedule() {
        this.schedules.forEach(Schedule::deleteCourse);
    }
}
