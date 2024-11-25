package com.konai.fxs.infra.config

import com.konasl.commonlib.springweb.config.rabbitmq.RabbitMqCommonConfig
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
class RabbitMQConfig {

    @Bean
    fun defaultRabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        return RabbitTemplate(connectionFactory).apply {
            this.messageConverter = Jackson2JsonMessageConverter()
        }
    }

}