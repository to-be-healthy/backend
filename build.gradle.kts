import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.2"
    id("io.freefair.lombok") version "8.6"
    id("io.spring.dependency-management") version "1.1.4"

    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.allopen") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"
    kotlin("plugin.lombok") version "1.9.22"
    kotlin("kapt") version "1.9.22"
    idea
}

kapt {
    keepJavacAnnotationProcessors = true
}

group = "com.to-be"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

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
    implementation("org.modelmapper:modelmapper:3.1.1")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.1.2")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.4.0")
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.1")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation(group = "com.google.code.gson", name = "gson", version = "2.9.0")
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-aop", version = "3.2.1")
    implementation(group = "com.google.guava", name = "guava", version = "33.0.0-jre")

    testImplementation("io.projectreactor:reactor-test")
    runtimeOnly("com.mysql:mysql-connector-j")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    compileOnly("io.jsonwebtoken:jjwt-api:0.11.5")
    compileOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    runtimeOnly("com.h2database:h2")

    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    testImplementation("io.rest-assured:rest-assured:5.3.0")

    implementation("com.amazonaws:aws-java-sdk-s3:1.12.696")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")

    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    implementation("jakarta.persistence:jakarta.persistence-api")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")


    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    testCompileOnly("io.github.microutils:kotlin-logging-jvm:3.0.5")

    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("jakarta.persistence:jakarta.persistence-api")
    kapt("org.projectlombok:lombok")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
    testImplementation("io.kotest:kotest-assertions-core:5.8.1")
    testImplementation("io.kotest:kotest-runner-junit5:5.8.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
