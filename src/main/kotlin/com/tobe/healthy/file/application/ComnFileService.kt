package com.tobe.healthy.file.application

import com.amazonaws.HttpMethod.PUT
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.Headers.S3_CANNED_ACL
import com.amazonaws.services.s3.model.CannedAccessControlList.PublicRead
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.tobe.healthy.common.Utils.FILE_TEMP_UPLOAD_TIMEOUT
import com.tobe.healthy.common.Utils.createFileName
import com.tobe.healthy.file.domain.dto.`in`.CommandUploadFile
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class ComnFileService(
    @Value("\${aws.s3.bucket-name}")
    private val bucket: String,
    private val amazonS3: AmazonS3
) {

    fun getPreSignedUrl(request: CommandUploadFile): String {
        val fileName = createPath(request.fileName)

        val generatePresignedUrlRequest = getGeneratePreSignedUrlRequest(bucket, fileName)

        val url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest)

        return url.toString()
    }

    private fun getGeneratePreSignedUrlRequest(bucket: String, fileName: String): GeneratePresignedUrlRequest {
        val generatePresignedUrlRequest = GeneratePresignedUrlRequest(bucket, fileName)
            .withMethod(PUT)
            .withExpiration(getPreSignedUrlExpiration())

        generatePresignedUrlRequest.addRequestParameter(S3_CANNED_ACL, PublicRead.toString())

        return generatePresignedUrlRequest
    }

    private fun getPreSignedUrlExpiration(): Date {
        val expiration = Date()
        var expTimeMillis = expiration.time
        expTimeMillis += FILE_TEMP_UPLOAD_TIMEOUT.toLong()
        expiration.setTime(expTimeMillis)
        return expiration
    }

    private fun createPath(fileName: String): String {
        val fileUUID = createFileName()
        return String.format("%s/%s", "temp", fileUUID + fileName.substring(fileName.lastIndexOf(".")))
    }
}