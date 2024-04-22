package com.tobe.healthy.config;

import static com.google.auth.oauth2.GoogleCredentials.fromStream;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.FirebaseOptions.Builder;
import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

	@PostConstruct
	public void init() {
		try {
			FileInputStream serviceAccount = new FileInputStream("src/main/resources/serviceAccountKey.json");
			FirebaseOptions options = new Builder()
				.setCredentials(fromStream(serviceAccount))
				.build();
			FirebaseApp.initializeApp(options);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
