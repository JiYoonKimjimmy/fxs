package com.konai.fxs.infra.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.konai.fxs.common.getCorrelationId
import com.konai.fxs.common.setCorrelationId
import com.konasl.commonlib.springweb.config.rabbitmq.RabbitMqCommonConfig
import com.konasl.commonlib.springweb.correlation.headerpropagator.CorrelationHeaderField
import org.springframework.amqp.core.MessagePostProcessor
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Profile

@Profile("!test")
@Import(RabbitMqCommonConfig::class)
@Configuration
class RabbitMQConfig(
    private val objectMapper: ObjectMapper
) {

    @Bean
    fun defaultRabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        val messagePostProcessor = MessagePostProcessor {
            it.messageProperties.headers[CorrelationHeaderField.CORRELATION_ID_HEADER_FIELD.getName()] = getCorrelationId()
            it
        }
        return RabbitTemplate(connectionFactory).apply {
            this.messageConverter = Jackson2JsonMessageConverter(objectMapper)
            this.setBeforePublishPostProcessors(messagePostProcessor)
        }
    }

    @Bean
    fun rabbitListenerContainerFactory(connectionFactory: ConnectionFactory): SimpleRabbitListenerContainerFactory {
        val messagePostProcessor = MessagePostProcessor {
            setCorrelationId(it.messageProperties.headers[CorrelationHeaderField.CORRELATION_ID_HEADER_FIELD.getName()] as String?)
            it
        }
        return SimpleRabbitListenerContainerFactory().apply {
            setConnectionFactory(connectionFactory)
            setMessageConverter(Jackson2JsonMessageConverter(objectMapper))
            setAfterReceivePostProcessors(messagePostProcessor)
        }
    }

}