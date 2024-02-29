package com.tobe.healthy.applicationform.domain.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationFormAddCommand {

    private Long scheduleId;
    private Long memberId;
    private String completed;

}
