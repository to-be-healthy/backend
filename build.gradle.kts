import org.gradle.api.JavaVersion.VERSION_17
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.2"
    id("io.freefair.lombok") version "8.6"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.gorylenko.gradle-git-properties") version "2.4.1"

    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
    kotlin("plugin.allopen") version "1.9.23"
    kotlin("plugin.jpa") version "1.9.23"
    kotlin("plugin.lombok") version "1.9.23"
    kotlin("kapt") version "1.9.23"
    idea
}

kapt {
    keepJavacAnnotationProcessors = true
}

group = "com.to-be"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("org.modelmapper:modelmapper:3.2.0")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.2.6")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.10.0")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation(group = "com.google.code.gson", name = "gson", version = "2.10")
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-aop", version = "3.3.0")
    implementation(group = "com.google.guava", name = "guava", version = "33.2.1-jre")
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.5")
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.744")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
    implementation("com.google.firebase:firebase-admin:9.3.0")
    implementation("org.imgscalr:imgscalr-lib:4.2")
    implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")

    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")

    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.rest-assured:rest-assured:5.4.0")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("com.h2database:h2")

    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    implementation("jakarta.persistence:jakarta.persistence-api")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.github.microutils:kotlin-logging-jvm:4.0.0-beta-2")

    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("jakarta.persistence:jakarta.persistence-api")
    kapt("org.projectlombok:lombok")
    kapt("com.querydsl:querydsl-apt:5.1.0:jakarta")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.0")
    testImplementation("io.mockk:mockk:1.13.10")
    testCompileOnly("io.github.microutils:kotlin-logging-jvm:4.0.0-beta-2")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

springBoot {
    buildInfo()
}