package com.tobe.healthy.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tobe.healthy.schedule.domain.TrainerScheduleInfo;

public interface TrainerScheduleInfoRepository extends JpaRepository<TrainerScheduleInfo, Long> {
	TrainerScheduleInfo findOneByTrainerId(Long trainerId);
}
