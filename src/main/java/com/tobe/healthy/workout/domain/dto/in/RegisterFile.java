package com.tobe.healthy.workout.domain.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RegisterFile {

    private String fileUrl;
    private int fileOrder;

    public RegisterFile(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
