package com.tobe.healthy.member.domain.dto.out;

import com.tobe.healthy.member.domain.entity.AlarmStatus;
import com.tobe.healthy.member.domain.entity.AlarmType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberChangeAlarmResult {
    private String type;
    private AlarmStatus status;

    public static MemberChangeAlarmResult from(AlarmType type, AlarmStatus status) {
        return MemberChangeAlarmResult.builder()
                .type(type.getDescription())
                .status(status)
                .build();
    }
}
