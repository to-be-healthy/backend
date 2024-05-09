package com.tobe.healthy.common;

import com.amazonaws.services.s3.model.ObjectMetadata;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.UUID;

public class Utils {

    public static String getAuthCode(int num) {
        return RandomStringUtils.randomNumeric(num);
    }

    public static String createFileUUID() {
        return System.currentTimeMillis() + "-" + UUID.randomUUID();
    }

    public static ObjectMetadata createObjectMetadata(Long fileSize, String contentType) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(contentType);
        return objectMetadata;
    }
}
