package com.tobe.healthy.home.presentation;

import com.tobe.healthy.common.CommonService;
import com.tobe.healthy.home.application.MemberHomeService;
import com.tobe.healthy.home.domain.dto.out.AttendanceResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberHomeController {

    private final MemberHomeService memberHomeService;
    private final CommonService commonService;

    @GetMapping("/attendance")
    public ResponseEntity<AttendanceResult> getAttendance(HttpServletRequest request){
        Long memberId = commonService.getRequesterId(request);
        AttendanceResult response = memberHomeService.getAttendance(memberId);
        return ResponseEntity.ok(response);
    }

}
