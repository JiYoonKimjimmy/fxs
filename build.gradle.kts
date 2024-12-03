import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    val kotlinVersion = "2.0.10"
    val springBootVersion = "3.3.4"

    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion

    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version "1.1.6"
    id("com.gorylenko.gradle-git-properties") version "2.4.2"
    id("com.epages.restdocs-api-spec") version "0.19.2"
    id("org.hidetake.swagger.generator") version "2.19.2"
    id("java-test-fixtures")
    jacoco
}

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
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")

    implementation("org.redisson:redisson-spring-boot-starter:3.34.1")
    implementation("org.apache.commons:commons-pool2:2.12.0")

    val jdslVersion = "3.5.3"
    implementation("com.linecorp.kotlin-jdsl:jpql-dsl:$jdslVersion")
    implementation("com.linecorp.kotlin-jdsl:jpql-render:$jdslVersion")
    implementation("com.linecorp.kotlin-jdsl:spring-data-jpa-support:$jdslVersion")

    // implementation("com.konasl.commonlibs:spring-web:7.0.1")
    // implementation("com.konasl.commonlibs:logger:7.0.1")
    // implementation("com.cubeone", "CubeOneAPI", "1.0.0")

    implementation(fileTree(rootProject.projectDir.resolve("libs")).matching {
        include("*.jar")
    })

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.4.0")

    runtimeOnly("com.oracle.database.jdbc:ojdbc11")

    // prometheus
    implementation("io.micrometer:micrometer-registry-prometheus")

    implementation("commons-io:commons-io:2.14.0")

    kapt("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    // spring docs
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("com.epages:restdocs-api-spec-mockmvc:0.19.2")

    // kotest
    val kotestVersion = "5.9.0"
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")

    // embedded-redis
    testImplementation("com.github.codemonstur:embedded-redis:1.4.3")
    // rabbitmq-mock
    testImplementation("com.github.fridujo:rabbitmq-mock:1.2.0")

    // fixture-monkey
    val fixtureMonkeyVersion = "1.1.2"
    testFixturesImplementation("com.navercorp.fixturemonkey:fixture-monkey-starter-kotlin:$fixtureMonkeyVersion")
    testFixturesImplementation("com.navercorp.fixturemonkey:fixture-monkey-kotest:$fixtureMonkeyVersion")
    testFixturesImplementation("com.navercorp.fixturemonkey:fixture-monkey-jackson:$fixtureMonkeyVersion")
    testFixturesImplementation("com.navercorp.fixturemonkey:fixture-monkey-jakarta-validation:$fixtureMonkeyVersion")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("com.h2database:h2")
}

configure<com.epages.restdocs.apispec.gradle.OpenApi3Extension> {
    setServer("http://localhost:12300/fxs")
    title = "FXS API"
    description = "FXS API"
    version = "0.0.1"
    format = "yaml"
    outputDirectory = "src/main/resources/static/"
}

gitProperties {
    val primary = "${project.property("version.primary")}"
    val major = "${project.property("version.major")}"
    val minor = "${project.property("version.minor")}"

    val buildVersion = listOf(primary, major, minor).joinToString(".")
    val buildTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    println("buildVersion = $buildVersion")
    println("buildDateTime = $buildTime")

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

    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude("**/v0/**")
            }
        })
    )

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