package com.tobe.healthy.schedule.repository;

import com.tobe.healthy.schedule.domain.entity.TrainerScheduleInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerScheduleInfoRepository extends JpaRepository<TrainerScheduleInfo, Long> {
    TrainerScheduleInfo findOneByTrainerId(Long trainerId);
}
