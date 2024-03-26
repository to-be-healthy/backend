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

@Entity
@Table(name = "trainer_member_mapping")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TrainerMemberMapping extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapping_id")
    private Long mappingId;

    private Long trainerId;
    private Long memberId;

    public static TrainerMemberMapping create(Long trainerId, Long memberId) {
        return TrainerMemberMapping.builder()
                .trainerId(trainerId)
                .memberId(memberId)
                .build();
    }

    @Builder
    public TrainerMemberMapping(Long mappingId, Long trainerId, Long memberId) {
        this.mappingId = mappingId;
        this.trainerId = trainerId;
        this.memberId = memberId;
    }
}
