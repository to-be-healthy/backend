package com.tobe.healthy.gym.repository;

import com.tobe.healthy.gym.domain.entity.GymMembership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GymMembershipRepository extends JpaRepository<GymMembership, Long> {
}
