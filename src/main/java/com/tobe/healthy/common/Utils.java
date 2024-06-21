package com.tobe.healthy.common;

import com.amazonaws.services.s3.model.ObjectMetadata;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.regex.Pattern;

public class Utils {
    public static final Integer EMAIL_AUTH_TIMEOUT = 3 * 60 * 1000; // 3분
    public static final Long FILE_TEMP_UPLOAD_TIMEOUT = 30 * 60 * 1000L; // 30분

    public static final DateTimeFormatter formatter_hmm = DateTimeFormatter.ofPattern("a h:mm");

    public static final String S3_DOMAIN = "https://to-be-healthy-bucket.s3.ap-northeast-2.amazonaws.com/";

    public static String getAuthCode(int num) {
        return RandomStringUtils.randomNumeric(num);
    }

    public static String createFileName() {
        return System.currentTimeMillis() + "-" + UUID.randomUUID();
    }

    public static String createFileName(String path) {
        return path + System.currentTimeMillis() + "-" + UUID.randomUUID();
    }

    public static String createFileName(String path, String extension) {
        return path + System.currentTimeMillis() + "-" + UUID.randomUUID() + extension;
    }

    public static String createProfileName(String path) {
        return path + System.currentTimeMillis() + "-" + UUID.randomUUID() + ".jpg";
    }

    public static boolean validatePassword(String password) {
        String regexp = "^[A-Za-z0-9]+$";
        if (password.length() < 8 || !Pattern.matches(regexp, password)) {
            return true;
        }
        return false;
    }

    public static boolean validateNameLength(String name) {
        if (name.length() < 2) {
            return true;
        }
        return false;
    }

    public static boolean validateNameFormat(String name) {
        String regexp = "^[가-힣A-Za-z]+$";
        if (Pattern.matches(regexp, name)) {
            return false;
        }
        return true;
    }

    public static boolean validateUserId(String userId) {
        return userId.length() < 4 || !StringUtils.hasText(userId);
    }

    public static <T extends Number> ObjectMetadata createObjectMetadata(T fileSize, String contentType) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize.longValue());
        objectMetadata.setContentType(contentType);
        return objectMetadata;
    }
}
