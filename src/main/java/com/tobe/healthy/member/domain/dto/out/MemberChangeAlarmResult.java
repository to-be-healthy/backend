package com.tobe.healthy.member.domain.dto.out;

import com.tobe.healthy.member.domain.entity.AlarmStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberChangeAlarmResult {
    private String type;
    private AlarmStatus status;

    public static MemberChangeAlarmResult from(String type, AlarmStatus status) {
        return MemberChangeAlarmResult.builder()
                .type(type)
                .status(status)
                .build();
    }
}
