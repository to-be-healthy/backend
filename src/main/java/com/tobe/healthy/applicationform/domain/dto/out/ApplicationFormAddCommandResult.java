package com.tobe.healthy.applicationform.domain.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApplicationFormAddCommandResult {

    private Long applicationFormId;
    private Long scheduleId;
    private Long memberId;
    private String completed;

}
