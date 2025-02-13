package com.konai.fxs.testsupport.rabbitmq

import com.konai.fxs.common.setCorrelationId
import com.konasl.commonlib.springweb.correlation.headerpropagator.CorrelationHeaderField
import org.springframework.amqp.core.MessagePostProcessor
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
        val messagePostProcessor = MessagePostProcessor {
            setCorrelationId(it.messageProperties.headers[CorrelationHeaderField.CORRELATION_ID_HEADER_FIELD.getName()] as String?)
            it
        }
        return SimpleRabbitListenerContainerFactory().apply {
            setConnectionFactory(connectionFactory)
            setMessageConverter(Jackson2JsonMessageConverter())
            setAfterReceivePostProcessors(messagePostProcessor)
        }
    }

}