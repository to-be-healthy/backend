package com.tobe.healthy.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import mu.two.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import java.io.FileInputStream

private val log = KotlinLogging.logger {}

@Configuration
class FirebaseConfig(
    @Value("\${firebase.admin-sdk.file:}")
    private val firebaseAdminsdkFile: String
) {

    @Bean
    fun initFirebase(): FirebaseApp? {
        if (firebaseAdminsdkFile.isBlank() || !File(firebaseAdminsdkFile).exists()) {
            log.warn { "Firebase Admin SDK 파일이 설정되지 않았거나 존재하지 않습니다. Firebase 초기화를 건너뜁니다." }
            return null
        }
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
