package com.tobe.healthy.course.domain.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.point.domain.entity.Calculation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;


@Entity
@Table(name = "course_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CourseHistory extends BaseTimeEntity<CourseHistory, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_history_id")
    private Long courseHistoryId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ColumnDefault("0")
    private int cnt;

    @Enumerated(STRING)
    @ColumnDefault("'PLUS'")
    private Calculation calculation;

    @Enumerated(STRING)
    @ColumnDefault("'COURSE_CREATE'")
    private CourseHistoryType type;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "trainer_id")
    private Member trainer;

    @Builder
    public CourseHistory(Course course, int cnt, Calculation calculation, CourseHistoryType type, Member trainer) {
        this.course = course;
        this.cnt = cnt;
        this.calculation = calculation;
        this.type = type;
        this.trainer = trainer;
    }

    public static CourseHistory create(Course course, int cnt, Calculation calculation, CourseHistoryType type, Member trainer) {
        return CourseHistory.builder()
                .course(course)
                .cnt(cnt)
                .calculation(calculation)
                .type(type)
                .trainer(trainer)
                .build();
    }
}
