package com.tobe.healthy.file;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterFile {
    private String fileUrl;
    private int fileOrder;
}
