package com.tobe.healthy.file.repository;


import com.tobe.healthy.file.domain.entity.FileInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileInfo, Long> {
	Optional<FileInfo> findByMemberId(Long memberId);
	void deleteAllByMemberId(Long memberId);
}
