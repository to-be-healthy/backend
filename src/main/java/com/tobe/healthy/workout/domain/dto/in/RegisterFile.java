package com.tobe.healthy.workout.domain.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterFile {
    private String fileUrl;
    private int fileOrder;
}
