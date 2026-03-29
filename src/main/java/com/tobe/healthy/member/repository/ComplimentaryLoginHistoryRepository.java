package com.tobe.healthy.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tobe.healthy.member.domain.entity.ComplimentaryLoginHistory;

public interface ComplimentaryLoginHistoryRepository extends JpaRepository<ComplimentaryLoginHistory, Long> {
}
