package com.tobe.healthy.trainer.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "trainer_member_mapping")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TrainerMemberMapping extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapping_id")
    private Long mappingId;
    private Long gymId;
    private Long trainerId;
    private Long memberId;

    private int lessonCnt;
    private int remainLessonCnt;

    @ColumnDefault("999")
    private int ranking = 999;

    public static TrainerMemberMapping create(Long gymId, Long trainerId, Long memberId, int lessonCnt, int remainLessonCnt) {
        return TrainerMemberMapping.builder()
                .gymId(gymId)
                .trainerId(trainerId)
                .memberId(memberId)
                .lessonCnt(lessonCnt)
                .remainLessonCnt(remainLessonCnt)
                .build();
    }

    public static TrainerMemberMapping create(Long trainerId, Long memberId, int lessonCnt, int remainLessonCnt) {
        return create(null, trainerId, memberId, lessonCnt, remainLessonCnt);
    }

    @Builder
    public TrainerMemberMapping(Long mappingId, Long gymId, Long trainerId, Long memberId, int lessonCnt, int remainLessonCnt) {
        this.mappingId = mappingId;
        this.gymId = gymId;
        this.trainerId = trainerId;
        this.memberId = memberId;
        this.lessonCnt = lessonCnt;
        this.remainLessonCnt = remainLessonCnt;
    }


}
