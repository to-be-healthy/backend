package com.tobe.healthy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.springframework.core.io.ClassPathResource;

public class Main {

	private static String key;
	private static String secretKey;

	public static void main(String[] args) {
		Properties properties = new Properties();

		try (InputStream input = new ClassPathResource("msg-api-key.properties").getInputStream()) {
			properties.load(input);
			key = properties.getProperty("msg-api-key");
			secretKey = properties.getProperty("msg-api-secret-key");
			System.out.println("msg = " + key);
			System.out.println("msg = " + secretKey);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
