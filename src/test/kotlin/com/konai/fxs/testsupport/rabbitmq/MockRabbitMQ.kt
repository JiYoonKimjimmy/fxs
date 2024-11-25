package com.konai.fxs.testsupport.rabbitmq

import com.github.fridujo.rabbitmq.mock.compatibility.MockConnectionFactoryFactory
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter

object MockRabbitMQ {

    private val connectionFactory: ConnectionFactory by lazy {
        MockConnectionFactoryFactory
            .build()
            .enableConsistentHashPlugin()
            .let(::CachingConnectionFactory)
    }

    private val rabbitAdmin: RabbitAdmin by lazy {
        RabbitAdmin(connectionFactory)
    }

    val rabbitTemplate: RabbitTemplate by lazy {
        RabbitTemplate(connectionFactory).apply { messageConverter = Jackson2JsonMessageConverter() }
    }

    fun binding(exchange: MockRabbitMQExchange) {
        exchange.binding(rabbitAdmin)
    }

}