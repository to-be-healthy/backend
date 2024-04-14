package com.tobe.healthy.course.domain.entity;

import com.tobe.healthy.common.EnumMapperType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CourseHistoryType implements EnumMapperType {

    COURSE_CREATE("수강권 생성"),
    PLUS_CNT("횟수 추가"),
    MINUS_CNT("횟수 차감"),
    ONE_LESSON("1회 수강권 지급"),
    RESERVATION("수업 예약"),
    RESERVATION_CANCEL("수업 예약 취소");

    private final String description;

    @Override
    public String getCode() {
        return name();
    }
}
