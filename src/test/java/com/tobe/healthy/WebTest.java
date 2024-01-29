package com.tobe.healthy;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest

public class WebTest {

	@Autowired
	private MockMvc mvc;

	@Test
	public void helloControllerTest() throws Exception {
		String content = "{\n"
			+ "    \"email\": \"seonwoo_jung@gmail.com\",\n"
			+ "    \"password\": \"12345678\"\n"
			+ "}";
		mvc.perform(post("/api/auth/join")
			.contentType(APPLICATION_JSON)
			.content(content))
			.andExpect(status().isOk());
		// 다른 여러 옵션 존재, 원하는 옵션을 추가해서 사용하면 됨
		// 참고 링크 : https://scshim.tistory.com/321
	}
}
