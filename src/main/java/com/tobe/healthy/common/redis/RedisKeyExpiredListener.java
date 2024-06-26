package com.tobe.healthy.common.redis;

import com.amazonaws.services.s3.AmazonS3;
import com.tobe.healthy.common.error.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import static com.tobe.healthy.common.Utils.S3_DOMAIN;
import static com.tobe.healthy.common.error.ErrorCode.FILE_REMOVE_ERROR;
import static com.tobe.healthy.common.redis.RedisKeyPrefix.TEMP_FILE_URI;

@Component
@Slf4j
public class RedisKeyExpiredListener extends KeyExpirationEventMessageListener {

	private final AmazonS3 amazonS3;

	@Value("${aws.s3.bucket-name}")
	private String bucketName;

	public RedisKeyExpiredListener(RedisMessageListenerContainer listenerContainer, AmazonS3 amazonS3) {
		super(listenerContainer);
        this.amazonS3 = amazonS3;
    }

	@Override
	public void onMessage(Message message, byte[] pattern) {
		String fileUrl = message.toString();
		if (fileUrl != null && fileUrl.startsWith(TEMP_FILE_URI.getDescription())) {
			try {
				String fileName = fileUrl.replaceAll(TEMP_FILE_URI.getDescription() + S3_DOMAIN, "");
				amazonS3.deleteObject(bucketName, fileName);
			} catch (Exception e) {
				log.error("onMessage error => {}", e.getMessage());
				throw new CustomException(FILE_REMOVE_ERROR);
			}
		}
		log.info("onMessage pattern => {} | {}", new String(pattern), message);
	}
}
