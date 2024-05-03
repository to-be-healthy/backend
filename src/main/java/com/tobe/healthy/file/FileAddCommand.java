package com.tobe.healthy.file;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class FileAddCommand {

    private FileUploadType fileUploadType;
    private List<MultipartFile> uploadFiles;

}
