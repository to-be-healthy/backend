package com.tobe.healthy.course.presentation;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.course.application.CourseService;
import com.tobe.healthy.course.domain.dto.in.CourseAddCommand;
import com.tobe.healthy.course.domain.dto.in.CourseUpdateCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/course/v1")
@Tag(name = "수강권 API", description = "수강권 API")
@Slf4j
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "수강권 등록", responses = {
            @ApiResponse(responseCode = "404", description = "존재하지 않는 트레이너"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원"),
            @ApiResponse(responseCode = "400", description = "이미 등록된 수강권이 존재"),
            @ApiResponse(responseCode = "400", description = "내 학생이 아닙니다."),
            @ApiResponse(responseCode = "200", description = "수강권을 등록한다.")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public ResponseHandler<Void> addCourse(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
                                           @RequestBody @Valid CourseAddCommand command) {
        courseService.addCourse(customMemberDetails.getMember().getId(), command);
        return ResponseHandler.<Void>builder()
                .message("수강권이 등록되었습니다.")
                .build();
    }

    @Operation(summary = "수강권 삭제", responses = {
            @ApiResponse(responseCode = "404", description = "존재하지 않는 트레이너"),
            @ApiResponse(responseCode = "200", description = "수강권을 삭제한다.")
    })
    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public ResponseHandler<Void> deleteCourse(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
                                              @Parameter(description = "수강권 ID") @PathVariable("courseId") Long courseId) {
        courseService.deleteCourse(customMemberDetails.getMember().getId(), courseId);
        return ResponseHandler.<Void>builder()
                .message("수강권이 삭제되었습니다.")
                .build();
    }

    @Operation(summary = "수강권 횟수 증가/차감", responses = {
            @ApiResponse(responseCode = "404", description = "존재하지 않는 트레이너"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 수강권"),
            @ApiResponse(responseCode = "200", description = "수강권 횟수를 증가 및 차감한다.")
    })
    @PatchMapping("/{courseId}")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public ResponseHandler<Void> updateCourse(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
                                              @Parameter(description = "수강권 ID") @PathVariable("courseId") Long courseId,
                                              @RequestBody @Valid CourseUpdateCommand command) {
        courseService.updateCourse(customMemberDetails.getMember().getId(), courseId, command);
        return ResponseHandler.<Void>builder()
                .message("수강권 횟수가 증가 및 차감 되었습니다.")
                .build();
    }

}
