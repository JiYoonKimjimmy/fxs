package com.konai.fxs.testsupport.annotation

import com.konai.fxs.testsupport.rabbitmq.MockRabbitMQConfig
import com.konai.fxs.testsupport.redis.EmbeddedRedisConfig
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional

@Import(value = [EmbeddedRedisConfig::class, MockRabbitMQConfig::class])
@TestDatabaseConfiguration
@AutoConfigureMockMvc
@Transactional
@SpringBootTest
annotation class CustomSpringBootTest