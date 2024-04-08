package com.tobe.healthy.file.repository;

import com.tobe.healthy.file.domain.entity.AwsS3File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AwsS3FileRepository extends JpaRepository<AwsS3File, Long> {
}
