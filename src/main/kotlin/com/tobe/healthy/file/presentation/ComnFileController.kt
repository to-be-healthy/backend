package com.tobe.healthy.file.presentation

import com.tobe.healthy.ApiResultResponse
import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.file.application.ComnFileService
import com.tobe.healthy.file.domain.dto.`in`.CommandUploadFile
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/file/v1")
class ComnFileController(
    private val comnFileService: ComnFileService
) {

    @PostMapping
    fun uploadFile(
        @RequestBody request: CommandUploadFile,
        @AuthenticationPrincipal member: CustomMemberDetails
    ): ApiResultResponse<MutableList<String>> {
        return ApiResultResponse(
            message = "presigned-uri을 생성하였습니다.",
            data = comnFileService.getPreSignedUrl(request)
        )
    }
}