package com.tobe.healthy.file;

import com.tobe.healthy.common.ResponseHandler;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.workout.domain.dto.out.WorkoutHistoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file/v1")
@Slf4j
public class FileController {

    private final FileService fileService;

    @Operation(summary = "첨부파일 등록", responses = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 입력"),
            @ApiResponse(responseCode = "200", description = "첨부파일을 반환한다.")
    })
    @PostMapping
    public ResponseHandler<List<RegisterFileResponse>> uploadFiles(@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
                                                          @Valid FileAddCommand command) {
        return ResponseHandler.<List<RegisterFileResponse>>builder()
                .data(fileService.uploadFiles(command.getFileUploadType(), command.getUploadFiles(), customMemberDetails.getMember()))
                .message("운동기록이 등록되었습니다.")
                .build();
    }

}
