package com.tobe.healthy.config.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException {
    private ErrorCode errorCode;

    public int getStatus() {
        return this.errorCode.getStatus();
    }

    public String getCode() {
        return this.errorCode.getCode();
    }

    public String getMessage() {
        return this.errorCode.getMessage();
    }
}
