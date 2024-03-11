package com.tobe.healthy.trainer.domain.entity;

import com.tobe.healthy.common.BaseEntity;
import com.tobe.healthy.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trainer_member_mapping")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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

}
