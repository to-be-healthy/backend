package com.tobe.healthy.gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tobe.healthy.gym.domain.Gym;

public interface GymRepository extends JpaRepository<Gym, Long> {
	Gym findByName(String name);
}
