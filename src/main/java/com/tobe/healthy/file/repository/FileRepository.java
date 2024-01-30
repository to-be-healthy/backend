package com.tobe.healthy.file.repository;


import com.tobe.healthy.file.domain.entity.Files;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<Files, Long> {
	Optional<Files> findByMemberId(Long memberId);
	void deleteAllByMemberId(Long memberId);
}
