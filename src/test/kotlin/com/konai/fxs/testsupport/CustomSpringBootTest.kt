package com.konai.fxs.testsupport

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional

@Import(value = [TestRedisConfig::class])
@TestDatabaseConfiguration
@AutoConfigureMockMvc
@Transactional
@SpringBootTest
annotation class CustomSpringBootTest