package com.tobe.healthy.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.tobe.healthy.log
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.FileInputStream
import javax.annotation.PostConstruct

@Service
class FirebaseConfig(
    @Value("\${firebase.admin-sdk.file}")
    private val firebaseAdminsdkFile: String
) {

    @PostConstruct
    fun initializeFCM() {
        FileInputStream(firebaseAdminsdkFile).use {
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(it))
                .build()
            log.info { "options => ${options}" }
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options)
                log.info { "Firebase application has been initialized"}
            }
        }
    }
}
