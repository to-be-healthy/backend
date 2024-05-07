package com.tobe.healthy.home.application;

import com.tobe.healthy.home.domain.dto.out.AttendanceResult;
import com.tobe.healthy.home.repository.MemberHomeRepositoryCustom;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberHomeService {

    private final MemberHomeRepositoryCustom memberHomeRepositoryCustom;

    public AttendanceResult getAttendance(long memberId) {
        LocalDate today = LocalDate.now();
        LocalDate startDay = today.withDayOfMonth(1);
        LocalDate endDay = today.withDayOfMonth(today.lengthOfMonth());

        long attendanceCnt = memberHomeRepositoryCustom.getAttendanceOfMonth(memberId, startDay, endDay.plusDays(1));
        long attendanceRate = (attendanceCnt * 100) / endDay.getDayOfMonth();
        log.info("endDay.getDayOfMonth(): {}, attendanceCnt: {}, attendanceRate: {}",
                endDay.getDayOfMonth(), attendanceCnt, attendanceRate);
        return new AttendanceResult(memberId, today.getMonthValue(), attendanceRate);
    }

}
