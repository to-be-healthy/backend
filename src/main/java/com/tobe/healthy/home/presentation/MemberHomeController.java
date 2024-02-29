package com.tobe.healthy.home.presentation;

import com.tobe.healthy.home.application.MemberHomeService;
import com.tobe.healthy.home.domain.dto.out.AttendanceResult;
import com.tobe.healthy.workout.domain.dto.out.WorkoutHistoryAddCommandResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberHomeController {

    private final MemberHomeService memberHomeService;

    @GetMapping("/members/{memberId}/attendance")
    public ResponseEntity<AttendanceResult> getAttendance(@PathVariable("memberId") Long memberId){
        AttendanceResult response = memberHomeService.getAttendance(memberId);
        return ResponseEntity.ok(response);
    }

}
