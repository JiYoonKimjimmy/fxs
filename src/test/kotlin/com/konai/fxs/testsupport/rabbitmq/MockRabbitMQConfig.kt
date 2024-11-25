package com.konai.fxs.testsupport.rabbitmq

import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class MockRabbitMQConfig {

    @Bean
    fun defaultRabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        return MockRabbitMQ.rabbitTemplate
    }

}