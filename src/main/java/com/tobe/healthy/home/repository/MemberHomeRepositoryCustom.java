package com.tobe.healthy.home.repository;

import java.time.LocalDate;

public interface MemberHomeRepositoryCustom {

    long getAttendanceOfMonth(long memberId, LocalDate startDay, LocalDate endDay);

}
