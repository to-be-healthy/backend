package com.tobe.healthy;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
public class MessageTest {

	@Autowired
	private JavaMailSender javaMailSender;

	@Test
	@DisplayName("메일을 전송한다.")
	void sendMail() {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
			mimeMessageHelper.setTo("laborlawseon@gmail.com"); // 메일 수신자
			mimeMessageHelper.setSubject("건강해짐 인증번호입니다."); // 메일 제목
			mimeMessageHelper.setText("안녕하세요. 건강해짐 인증번호는 654321 입니다. \n확인후 입력해 주세요.", false); // 메일 본문 내용, HTML 여부
			javaMailSender.send(mimeMessage);
			log.info("Success!!");
		} catch (MessagingException e) {
			log.info("fail!!");
			throw new RuntimeException(e);
		}
	}

	@Test
	void generateAuthCode() {
	    // given
		Random random = new Random();
		StringBuffer buffer = new StringBuffer();
		int num = 0;

		while(buffer.length() < 6) {
			num = random.nextInt(10);
			buffer.append(num);
		}

		log.info(buffer.toString());
	}

	@Test
	void getRandomStr() {
	    // given
		String randomStr = RandomStringUtils.random(12, true, true);
		log.info("randomStr => {}", randomStr);

	}
}
