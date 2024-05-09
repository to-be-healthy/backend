package com.tobe.healthy.common;

import org.apache.commons.lang3.RandomStringUtils;

public class Utils {
    public static String getAuthCode(int num) {
        return RandomStringUtils.randomNumeric(num);
    }
}
