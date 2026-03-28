package com.tobe.healthy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> {

    @Builder.Default
    private HttpStatus status = HttpStatus.OK;

    private String message;

    private T data;

    public ApiResult(String message, T data) {
        this.status = HttpStatus.OK;
        this.message = message;
        this.data = data;
    }
}
