package com.tobe.healthy.course.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "course")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Course extends BaseTimeEntity<Course, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Member trainer;

    private int lessonCnt;
    private int remainLessonCnt;

    @Builder
    public Course(Member member, Member trainer, int lessonCnt, int remainLessonCnt) {
        this.member = member;
        this.trainer = trainer;
        this.lessonCnt = lessonCnt;
        this.remainLessonCnt = remainLessonCnt;
    }

    public static Course create(Member member, Member trainer, int lessonCnt, int remainLessonCnt) {
        return Course.builder()
                .member(member)
                .trainer(trainer)
                .lessonCnt(lessonCnt)
                .remainLessonCnt(remainLessonCnt)
                .build();
    }
}
