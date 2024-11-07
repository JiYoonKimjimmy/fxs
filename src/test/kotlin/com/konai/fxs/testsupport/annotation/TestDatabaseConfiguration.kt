package com.konai.fxs.testsupport.annotation

import com.konai.fxs.infra.config.JpaAuditorConfig
import com.linecorp.kotlinjdsl.support.spring.data.jpa.autoconfigure.KotlinJdslAutoConfiguration
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@Import(value = [
    JpaAuditorConfig::class,
    KotlinJdslAutoConfiguration::class
])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = ["spring.profiles.active=test"])
@ActiveProfiles("test")
annotation class TestDatabaseConfiguration