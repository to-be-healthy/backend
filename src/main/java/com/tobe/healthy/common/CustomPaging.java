package com.tobe.healthy.common;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class CustomPaging<T> {

    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private Long totalElements;
    private Boolean isLast;
    private T mainData;


    public CustomPaging(List<T> content, int pageNumber, int pageSize, int totalPages, Long totalElements, Boolean isLast) {
        this.content = content.isEmpty() ? null : content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.isLast = isLast;
    }

}
