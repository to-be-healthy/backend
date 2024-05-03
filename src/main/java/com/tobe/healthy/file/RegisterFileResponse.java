package com.tobe.healthy.file;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterFileResponse {
    private String fileUrl;
    private String fileName;
    private int fileOrder;
}
