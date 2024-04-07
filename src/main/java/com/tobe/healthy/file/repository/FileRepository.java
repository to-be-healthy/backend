package com.tobe.healthy.file.repository;


import com.tobe.healthy.file.domain.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<Profile, Long> {
	Optional<Profile> findByMemberId(Long memberId);
	List<Profile> findAllByLessonHistoryIdIn(List<Long> id);
	Profile findByLessonHistoryId(Long id);
}
