package com.konai.fxs.testsupport

import com.konai.fxs.infra.config.JpaAuditorConfig
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@Import(JpaAuditorConfig::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
annotation class TestDatabaseConfiguration