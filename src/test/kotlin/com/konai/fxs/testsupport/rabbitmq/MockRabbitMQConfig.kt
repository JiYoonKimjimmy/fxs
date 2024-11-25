package com.konai.fxs.testsupport.rabbitmq

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class MockRabbitMQConfig {

    @Bean
    fun connectionFactory(): ConnectionFactory {
        return MockRabbitMQ.connectionFactory
            .also { MockRabbitMQ.binding() }
    }

    @Bean
    fun defaultRabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        return MockRabbitMQ.rabbitTemplate
    }

    @Bean
    fun rabbitListenerContainerFactory(connectionFactory: ConnectionFactory): SimpleRabbitListenerContainerFactory {
        return SimpleRabbitListenerContainerFactory().apply {
            setConnectionFactory(connectionFactory)
            setMessageConverter(Jackson2JsonMessageConverter())
        }
    }

}