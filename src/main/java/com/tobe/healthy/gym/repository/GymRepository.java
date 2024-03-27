package com.tobe.healthy.gym.repository;

import com.tobe.healthy.gym.domain.entity.Gym;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GymRepository extends JpaRepository<Gym, Long> {
}
