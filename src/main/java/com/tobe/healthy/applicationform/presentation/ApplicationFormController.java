package com.tobe.healthy.applicationform.presentation;
import com.tobe.healthy.applicationform.application.ApplicationFormService;
import com.tobe.healthy.applicationform.domain.dto.in.ApplicationFormAddCommand;
import com.tobe.healthy.applicationform.domain.dto.out.ApplicationFormAddCommandResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/application-forms")
@Slf4j
public class ApplicationFormController {

    private final ApplicationFormService applicationFormService;

    @PostMapping
    public ResponseEntity<ApplicationFormAddCommandResult> addApplicationForm(@RequestBody ApplicationFormAddCommand request) {
        ApplicationFormAddCommandResult response = applicationFormService.addApplicationForm(request);
        return ResponseEntity.ok(response);
    }

}
