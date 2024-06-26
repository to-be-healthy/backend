package com.tobe.healthy.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Duration.ofMillis;

@Configuration
public class RestTemplateConfig {

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
		return restTemplateBuilder
			.requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
			.setConnectTimeout(ofMillis(5000)) // connection-timeout
			.setReadTimeout(ofMillis(5000)) // read-timeout
			.additionalMessageConverters(new StringHttpMessageConverter(UTF_8))
			.build();
	}
}
