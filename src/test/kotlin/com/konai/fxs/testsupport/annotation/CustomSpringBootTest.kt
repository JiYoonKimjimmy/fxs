package com.konai.fxs.testsupport.annotation

import com.konai.fxs.testsupport.redis.TestRedisConfig
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