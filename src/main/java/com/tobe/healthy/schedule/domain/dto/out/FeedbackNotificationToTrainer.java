package com.tobe.healthy.schedule.domain.dto.out;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FeedbackNotificationToTrainer {

    private Long trainerId;
    private Long count;

    @QueryProjection
    public FeedbackNotificationToTrainer(Long trainerId, Long count) {
        this.trainerId = trainerId;
        this.count = count;
    }
}
