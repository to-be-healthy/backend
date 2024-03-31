package com.tobe.healthy.gym.repository;

import com.tobe.healthy.gym.domain.entity.Gym;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GymRepository extends JpaRepository<Gym, Long> {
    Optional<Gym> findByName(String name);
    Optional<Gym> findByIdAndJoinCode(Long id, int joinCode);
}
