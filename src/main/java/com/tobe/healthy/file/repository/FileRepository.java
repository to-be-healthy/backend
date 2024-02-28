package com.tobe.healthy.file.repository;


import com.tobe.healthy.file.domain.entity.Profile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<Profile, Long> {
	Optional<Profile> findByMemberId(Long memberId);
}
