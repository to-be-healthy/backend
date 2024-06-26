package com.tobe.healthy.schedule.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.member.domain.entity.MemberType;
import com.tobe.healthy.schedule.application.CommonScheduleService;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleIdInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.tobe.healthy.member.domain.entity.MemberType.TRAINER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule/v1")
@Slf4j
@Valid
@Tag(name = "03-03.수업 공통 API", description = "수업 공통 API")
public class CommonScheduleController {

    private final CommonScheduleService commonScheduleService;

    @Operation(summary = "트레이너 또는 학생이 수업을 신청한다.", description = "트레이너 또는 학생이 수업을 신청한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "수업 신청 완료"),
                    @ApiResponse(responseCode = "404(1)", description = "해당 수업이 존재하지 않습니다."),
                    @ApiResponse(responseCode = "404(2)", description = "신청 할 수 없는 수업입니다.")
            })
    @PostMapping("/{scheduleId}")
    public ResponseHandler<ScheduleIdInfo> reserveSchedule(@PathVariable Long scheduleId,
                                                           @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
        ScheduleIdInfo result = commonScheduleService.reserveSchedule(scheduleId, customMemberDetails.getMemberId());
        return ResponseHandler.<ScheduleIdInfo>builder()
                .data(result)
                .message(result.getScheduleTime() + " 수업이 예약되었습니다.")
                .build();
    }

    @Operation(summary = "트레이너 또는 학생이 수업을 취소한다.", description = "트레이너 또는 학생이 등록한 수업을 취소한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "해당 수업을 취소하였습니다."),
                    @ApiResponse(responseCode = "404", description = "해당 수업이 존재하지 않습니다.")
            })
    @DeleteMapping("/{scheduleId}")
    public ResponseHandler<ScheduleIdInfo> cancelScheduleForMember(@PathVariable Long scheduleId,
                                                                   @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
        ScheduleIdInfo result;
        if(TRAINER.equals(customMemberDetails.getMember().getMemberType())){
            result = commonScheduleService.cancelMemberScheduleForce(scheduleId, customMemberDetails.getMemberId());
        }else{
            result = commonScheduleService.cancelMemberSchedule(scheduleId, customMemberDetails.getMemberId());
        }
        return ResponseHandler.<ScheduleIdInfo>builder()
                .data(result)
                .message(result.getScheduleTime() + " 수업이 취소되었습니다.")
                .build();
    }
}
