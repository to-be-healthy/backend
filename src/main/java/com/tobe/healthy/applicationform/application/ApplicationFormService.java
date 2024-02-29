package com.tobe.healthy.applicationform.application;

import com.tobe.healthy.applicationform.domain.dto.in.ApplicationFormAddCommand;
import com.tobe.healthy.applicationform.domain.dto.out.ApplicationFormAddCommandResult;
import com.tobe.healthy.applicationform.domain.entity.ApplicationForm;
import com.tobe.healthy.applicationform.repository.ApplicationFormRepository;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.config.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationFormService {

    private final ModelMapper modelMapper;
    private final ApplicationFormRepository applicationFormRepository;

    @Transactional
    public ApplicationFormAddCommandResult addApplicationForm(ApplicationFormAddCommand request) {
        ApplicationForm form = modelMapper.map(request, ApplicationForm.class);
        applicationFormRepository.save(form);
        form = applicationFormRepository.findById(form.getApplicationFormId())
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_FORM_NOT_FOUND));
        return new ApplicationFormAddCommandResult(form.getApplicationFormId(),
                form.getScheduleId(),
                form.getMemberId(),
                form.getCompleted());
    }

}
