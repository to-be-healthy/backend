package com.tobe.healthy.common;

import lombok.Builder;

@Builder
public record ResponseHandler<T>(String message, T data) {

}
