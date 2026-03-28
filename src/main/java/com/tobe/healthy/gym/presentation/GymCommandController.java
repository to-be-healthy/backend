package com.tobe.healthy.gym.presentation;

import com.tobe.healthy.ApiResult;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.gym.application.GymCommandService;
import com.tobe.healthy.gym.domain.dto.in.CommandRegisterGym;
import com.tobe.healthy.gym.domain.dto.in.CommandSelectMyGym;
import com.tobe.healthy.gym.domain.dto.out.CommandRegisterGymResult;
import com.tobe.healthy.gym.domain.dto.out.CommandSelectMyGymResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gyms/v1")
@RequiredArgsConstructor
@Tag(name = "04-01.헬스장 API", description = "헬스장 조회 API")
public class GymCommandController {

    private final GymCommandService gymCommandService;

    @Operation(
            summary = "관리자 또는 트레이너가 헬스장을 등록한다.",
            responses = @ApiResponse(responseCode = "200", description = "헬스장을 등록하였습니다.")
    )
    @PostMapping
    public ApiResult<CommandRegisterGymResult> registerGym(
            @RequestBody CommandRegisterGym request) {
        return ApiResult.<CommandRegisterGymResult>builder()
                .data(gymCommandService.registerGym(request))
                .message("헬스장을 등록하였습니다.")
                .build();
    }

    @Operation(
            summary = "학생 또는 트레이너가 내가 다니는 헬스장으로 등록한다.",
            responses = {
                    @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "404", description = "헬스장을 찾을 수 없습니다."),
                    @ApiResponse(responseCode = "200", description = "내 헬스장으로 등록하였습니다.")
            }
    )
    @PostMapping("/{gymId}")
    public ApiResult<CommandSelectMyGymResult> selectMyGym(
            @PathVariable Long gymId,
            @RequestBody(required = false) CommandSelectMyGym request,
            @AuthenticationPrincipal CustomMemberDetails member) {
        return ApiResult.<CommandSelectMyGymResult>builder()
                .data(gymCommandService.selectMyGym(gymId, request, member.getMemberId()))
                .message("내 헬스장으로 등록되었습니다.")
                .build();
    }
}
