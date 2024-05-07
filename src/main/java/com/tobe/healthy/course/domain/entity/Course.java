package com.tobe.healthy.course.domain.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.course.domain.dto.in.CourseUpdateCommand;
import com.tobe.healthy.member.domain.entity.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


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
