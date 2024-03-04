package com.tobe.healthy.member.application;

import static java.io.File.separator;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.util.StringUtils.cleanPath;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@Transactional
@Slf4j
public class ProfileTest {

	@Autowired
	private RestTemplate restTemplate;

//	@Test
//	@DisplayName("특정 URL에 있는 사진을 다운받는다.")
//	void downloadProfile() throws IOException {
//	    // given
//		String url = "http://k.kakaocdn.net/dn/b1Cz3X/btsEmDr0U6L/tiggCiB6zoSE9iGA2EV1w0/img_640x640.jpg";
//		byte[] image = restTemplate.getForObject(url, byte[].class);
//		String uploadDir = "upload";
//
//		String fileName = url.substring(url.lastIndexOf("/") + 1);
//
//		Path copyOfLocation = Paths.get(uploadDir + separator + cleanPath(fileName));
//		Files.copy(new ByteArrayInputStream(image), copyOfLocation, REPLACE_EXISTING);
//
//		// 파일의 용량 구하기
//		long fileSize = Files.size(copyOfLocation);
//
//		String extension = fileName.substring(fileName.lastIndexOf("."));
//		String fileName2 = fileName.substring(0, fileName.lastIndexOf("."));
//
//		log.info("copyOfLocation => {}", uploadDir + separator);
//		log.info("fileSize => " + fileSize);
//		log.info("fileName => " + fileName);
//		log.info("확장자 제외 파일명 => {}", fileName2);
//		log.info("extension => {}", extension);
//	}
}
