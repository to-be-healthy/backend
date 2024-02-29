package com.tobe.healthy.applicationform.repository;

import com.tobe.healthy.applicationform.domain.entity.ApplicationForm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationFormRepository extends JpaRepository<ApplicationForm, Long> {

}
