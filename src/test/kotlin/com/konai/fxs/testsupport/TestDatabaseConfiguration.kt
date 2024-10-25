package com.konai.fxs.testsupport

import com.konai.fxs.infra.config.JpaAuditorConfig
import com.linecorp.kotlinjdsl.support.spring.data.jpa.autoconfigure.KotlinJdslAutoConfiguration
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@Import(value = [
    JpaAuditorConfig::class,
    KotlinJdslAutoConfiguration::class
])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
annotation class TestDatabaseConfiguration