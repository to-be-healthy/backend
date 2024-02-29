package com.tobe.healthy.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultFormatType {

    SUCCESS("1", "Y", true)
    , FAIL("0", "N", false);

    private final String resultNum;
    private final String resultAlphabet;
    private final boolean isResult;

}
