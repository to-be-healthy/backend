package com.tobe.healthy.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream

@Configuration
class FirebaseConfig(
    @Value("\${firebase.admin-sdk.file}")
    private val firebaseAdminsdkFile: String
) {

    @Bean
    fun initFirebase(): FirebaseApp {
        FileInputStream(firebaseAdminsdkFile).use {
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(it))
                .build()
            if (FirebaseApp.getApps().isEmpty()) {
                return FirebaseApp.initializeApp(options)
            }
            return FirebaseApp.getInstance()
        }
    }
}
