package com.tobe.healthy.course.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.course.domain.dto.in.CourseUpdateCommand;
import com.tobe.healthy.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.tobe.healthy.config.error.ErrorCode.LESSON_CNT_NOT_VALID;
import static jakarta.persistence.FetchType.LAZY;


@Entity
@Table(name = "course")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Course extends BaseTimeEntity<Course, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "trainer_id")
    private Member trainer;

    private int totalLessonCnt;
    private int remainLessonCnt;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private final List<CourseHistory> courseHistories = new ArrayList<>();

    @Builder
    public Course(Member member, Member trainer, int totalLessonCnt, int remainLessonCnt) {
        this.member = member;
        this.trainer = trainer;
        this.totalLessonCnt = totalLessonCnt;
        this.remainLessonCnt = remainLessonCnt;
    }

    public static Course create(Member member, Member trainer, int totalLessonCnt, int remainLessonCnt) {
        if(totalLessonCnt < 1 || remainLessonCnt < 1) throw new CustomException(LESSON_CNT_NOT_VALID);
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

}
