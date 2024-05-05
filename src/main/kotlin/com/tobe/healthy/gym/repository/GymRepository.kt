package com.tobe.healthy.gym.repository

import com.tobe.healthy.gym.domain.entity.Gym
import org.springframework.data.jpa.repository.JpaRepository

interface GymRepository : JpaRepository<Gym, Long> {
    fun findByName(name: String): Gym?
}
