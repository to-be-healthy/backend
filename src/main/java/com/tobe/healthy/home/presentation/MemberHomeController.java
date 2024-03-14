package com.tobe.healthy.home.presentation;

import com.tobe.healthy.common.CommonService;
import com.tobe.healthy.home.application.MemberHomeService;
import com.tobe.healthy.home.domain.dto.out.AttendanceResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class MemberHomeController {

    private final MemberHomeService memberHomeService;

    @GetMapping("/attendance")
    public ResponseEntity<?> getAttendance(@RequestParam("memberId") Long memberId){
        AttendanceResult response = memberHomeService.getAttendance(memberId);
        return ResponseEntity.ok(response);
    }

}
