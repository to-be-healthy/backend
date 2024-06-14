package com.tobe.healthy.workout.application;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.tobe.healthy.common.Utils;
import com.tobe.healthy.common.redis.RedisService;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.diet.domain.entity.Diet;
import com.tobe.healthy.diet.domain.entity.DietFiles;
import com.tobe.healthy.diet.domain.entity.DietType;
import com.tobe.healthy.diet.repository.DietFileRepository;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.workout.domain.dto.in.RegisterFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.tobe.healthy.common.Utils.*;
import static com.tobe.healthy.common.redis.RedisKeyPrefix.TEMP_FILE_URI;
import static com.tobe.healthy.config.error.ErrorCode.FILE_REMOVE_ERROR;
import static com.tobe.healthy.config.error.ErrorCode.FILE_UPLOAD_ERROR;
import static java.util.UUID.randomUUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FileService {
    private final DietFileRepository dietFileRepository;
    private final AmazonS3 amazonS3;
    private final RedisService redisService;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;


    // 1. 파일을 AWS S3에 업로드 후 업로드 주소 반환
    public List<RegisterFile> uploadFiles(String folder, List<MultipartFile> uploadFiles, Member member) {
        List<RegisterFile> uploadFile = new ArrayList<>();
        int fileOrder = 0;
        for (MultipartFile file : uploadFiles) {
            if (!file.isEmpty()) {
                try (InputStream inputStream = file.getInputStream()) {

                    //원본파일
                    ObjectMetadata objectMetadata = Utils.createObjectMetadata(file.getSize(), file.getContentType());
                    String fileName = System.currentTimeMillis() + "-" + randomUUID();
                    String savedFileName =folder + "/" + fileName;
                    amazonS3.putObject(
                            bucketName,
                            savedFileName,
                            inputStream,
                            objectMetadata
                    );

                    //썸네일
                    File thumnail = makeThumbnail(file.getInputStream(), fileName);
                    String thumSavedFileName = THUMB_PREFIX + fileName;
                    ObjectMetadata thumnailMetadata = Utils.createObjectMetadata(thumnail.length(), file.getContentType());
                    String savedThumFileName = folder + "/" + thumSavedFileName;
                    amazonS3.putObject(
                            bucketName,
                            savedThumFileName,
                            new FileInputStream(thumnail),
                            thumnailMetadata
                    );
                    String fileUrl = amazonS3.getUrl(bucketName, savedThumFileName).toString();
                    redisService.setValuesWithTimeout(TEMP_FILE_URI.getDescription() + fileUrl, member.getId().toString(), FILE_TEMP_UPLOAD_TIMEOUT); // 30분
                    uploadFile.add(new RegisterFile(fileUrl, ++fileOrder));
                    if(thumnail.exists()) thumnail.delete();
                } catch (Exception e) {
                    log.error("error => {}", e.getStackTrace()[0]);
                }
            }
        }
        return uploadFile;
    }

    private File makeThumbnail(InputStream inputStream, String fileName) throws Exception {
        // 저장된 원본파일로부터 BufferedImage 객체를 생성합니다.
        BufferedImage srcImg = ImageIO.read(inputStream);

        // 썸네일의 너비와 높이 입니다.
        int dw = 100, dh = 100;

        // 원본 이미지의 너비와 높이 입니다.
        int ow = srcImg.getWidth();
        int oh = srcImg.getHeight();

        // 원본 너비를 기준으로 하여 썸네일의 비율로 높이를 계산합니다.
        int nw = ow; int nh = (ow * dh) / dw;

        // 계산된 높이가 원본보다 높다면 crop이 안되므로
        // 원본 높이를 기준으로 썸네일의 비율로 너비를 계산합니다.
        if(nh > oh) {
            nw = (oh * dw) / dh;
            nh = oh;
        }

        // 계산된 크기로 원본이미지를 가운데에서 crop 합니다.
        BufferedImage cropImg = Scalr.crop(srcImg, (ow-nw)/2, (oh-nh)/2, nw, nh);

        // crop된 이미지로 썸네일을 생성합니다.
        BufferedImage destImg = Scalr.resize(cropImg, dw, dh);

        // 썸네일을 저장합니다. 이미지 이름 앞에 "THUMB_" 를 붙여 표시했습니다.
        String thumbName = THUMB_PREFIX + fileName;
        File thumbFile = new File(thumbName);
        ImageIO.write(destImg, "jpg", thumbFile);
        return thumbFile;
    }

    public void deleteDietFile(String fileName) {
        try {
            amazonS3.deleteObject(bucketName, "diet/" + fileName);
            amazonS3.deleteObject(bucketName, "diet/" + THUMB_PREFIX + fileName);
        } catch (Exception e) {
            log.error("error => {}", e.getStackTrace()[0]);
            throw new CustomException(FILE_REMOVE_ERROR);
        }
    }

    public void deleteHistoryFile(String fileName) {
        try {
            amazonS3.deleteObject(bucketName, "workout-history/" + fileName);
        } catch (Exception e) {
            log.error("error => {}", e.getStackTrace()[0]);
            throw new CustomException(FILE_REMOVE_ERROR);
        }
    }

    public void uploadDietFile(Diet diet, DietType type, MultipartFile uploadFile) {
        if (!uploadFile.isEmpty()) {
            try {
                String savedFileName = "diet/" + System.currentTimeMillis() + "-" + randomUUID();

                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentLength(uploadFile.getSize());
                objectMetadata.setContentType(uploadFile.getContentType());
                amazonS3.putObject(bucketName, savedFileName, uploadFile.getInputStream(), objectMetadata);
                String fileUrl = amazonS3.getUrl(bucketName, savedFileName).toString();
                dietFileRepository.save(DietFiles.create(diet, fileUrl, type));
            } catch (IOException e) {
                log.error("error => {}", e.getStackTrace()[0]);
                throw new CustomException(FILE_UPLOAD_ERROR);
            }
        }
    }

}
