package com.tobe.healthy.file.presentation;

import com.tobe.healthy.ApiResult;
import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.file.application.ComnFileService;
import com.tobe.healthy.file.domain.dto.in.CommandUploadFile;
import com.tobe.healthy.workout.domain.dto.in.RegisterFile;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/file/v1")
@RequiredArgsConstructor
public class ComnFileController {

    private final ComnFileService comnFileService;

    @PostMapping
    public ApiResult<List<RegisterFile>> uploadFile(
            @RequestBody CommandUploadFile request,
            @AuthenticationPrincipal CustomMemberDetails member) {
        return new ApiResult<>(HttpStatus.OK, "presigned-uri을 생성하였습니다.", comnFileService.getPreSignedUrl(request));
    }
}
