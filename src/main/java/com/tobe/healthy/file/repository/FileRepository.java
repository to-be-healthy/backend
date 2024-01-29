package com.tobe.healthy.file.repository;


import com.tobe.healthy.file.domain.Files;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<Files, Long> {

}
