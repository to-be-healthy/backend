package com.tobe.healthy.home.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.home.application.HomeService;
import com.tobe.healthy.member.domain.dto.out.StudentHomeResult;
import com.tobe.healthy.member.domain.dto.out.TrainerHomeResult;
import com.tobe.healthy.point.application.PointService;
import com.tobe.healthy.schedule.entity.in.TrainerTodayScheduleSearchCond;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home/v1")
@Slf4j
public class HomeController {

    private final HomeService homeService;
    private final PointService pointService;

    @Operation(summary = "학생 홈 조회", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "학생 홈을 반환한다.")
    })
    @GetMapping("/student")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseHandler<StudentHomeResult> getStudentHome(@AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
        return ResponseHandler.<StudentHomeResult>builder()
                .data(homeService.getStudentHome(customMemberDetails.getMemberId()))
                .message("학생 홈이 조회되었습니다.")
                .build();
    }

    @Operation(summary = "트레이너 홈 조회", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "트레이너 홈을 반환한다.")
    })
    @GetMapping("/trainer")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public ResponseHandler<TrainerHomeResult> getTrainerHome(@RequestBody TrainerTodayScheduleSearchCond request,
                                                             @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
        return ResponseHandler.<TrainerHomeResult>builder()
                .data(homeService.getTrainerHome(request, customMemberDetails.getMemberId()))
                .message("트레이너 홈이 조회되었습니다.")
                .build();
    }

}