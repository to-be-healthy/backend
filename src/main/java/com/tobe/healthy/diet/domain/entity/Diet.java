package com.tobe.healthy.diet.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.file.domain.entity.DietFile;
import com.tobe.healthy.file.domain.entity.WorkoutHistoryFile;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryDto;
import com.tobe.healthy.workout.domain.entity.CompletedExercise;
import com.tobe.healthy.workout.domain.entity.WorkoutHistoryComment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "diet")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class Diet extends BaseTimeEntity<Diet, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diet_id")
    private Long dietId;

    private String content;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Member trainer;

    @ColumnDefault("false")
    @Builder.Default
    private Boolean delYn = false;

    @ColumnDefault("0")
    @Builder.Default
    private Long likeCnt = 0L;

    @Builder.Default
    @OneToMany(mappedBy = "diet", cascade = CascadeType.ALL)
    private List<DietFile> dietFiles = new ArrayList<>();

}