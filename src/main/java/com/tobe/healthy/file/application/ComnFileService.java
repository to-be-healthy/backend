package com.tobe.healthy.file.application;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.tobe.healthy.file.domain.dto.in.CommandUploadFile;
import com.tobe.healthy.workout.domain.dto.in.RegisterFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.tobe.healthy.common.Utils.FILE_TEMP_UPLOAD_TIMEOUT;
import static com.tobe.healthy.common.Utils.createFileName;

@Service
@RequiredArgsConstructor
public class ComnFileService {

    @Value("${aws.s3.bucket-name}")
    private String bucket;

    private final AmazonS3 amazonS3;

    public List<RegisterFile> getPreSignedUrl(CommandUploadFile request) {
        List<RegisterFile> uploadFile = new ArrayList<>();
        int fileOrder = 0;
        for (String fileName : request.getFileNames()) {
            String path = createPath(fileName);
            GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePreSignedUrlRequest(bucket, path);
            URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
            uploadFile.add(new RegisterFile(url.toString(), ++fileOrder));
        }
        return uploadFile;
    }

    private GeneratePresignedUrlRequest getGeneratePreSignedUrlRequest(String bucket, String fileName) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(getPreSignedUrlExpiration());

        generatePresignedUrlRequest.addRequestParameter(Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead.toString());

        return generatePresignedUrlRequest;
    }

    private Date getPreSignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += FILE_TEMP_UPLOAD_TIMEOUT;
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    private String createPath(String fileName) {
        String fileUUID = createFileName();
        return String.format("%s/%s", "temp", fileUUID + fileName.substring(fileName.lastIndexOf(".")));
    }
}
