import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    val kotlinVersion = "2.0.10"
    val springBootVersion = "3.3.4"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version "1.1.6"
    id("com.gorylenko.gradle-git-properties") version "2.4.2"
    id("java-test-fixtures")
    jacoco
}

group = "com.konai"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2023.0.3"

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.amqp:spring-rabbit-test")
    testImplementation("com.ninja-squad:springmockk:4.0.2")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("io.kotest:kotest-property:5.8.0")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

gitProperties {
    val projectName = project.name
    val primary = "${project.property("version.primary")}"
    val major = "${project.property("version.major")}"
    val minor = "${project.property("version.minor")}"

    val buildVersion = listOf(primary, major, minor).joinToString(".")
    val buildTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    println()
    println("projectName   : $projectName")
    println("buildVersion  : $buildVersion")
    println("buildDateTime : $buildTime")

    customProperty("git.build.version", buildVersion)
    customProperty("git.build.time", buildTime)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.jacocoTestReport {
    reports {
        html.required = true
        xml.required = false
        csv.required = false
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }

    finalizedBy("jacocoTestCoverageVerification")
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            enabled = false
            limit {
                minimum = "0.10".toBigDecimal()
            }
        }
    }
}

sourceSets {
    testFixtures {
        kotlin.srcDir("src/test/kotlin/fixtures")
    }
}