package com.konai.fxs.message

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class MessageQueuePublisherImpl(
    private val defaultRabbitTemplate: RabbitTemplate
) : MessageQueuePublisher {

    override fun sendDirectMessage(exchange: MessageQueueExchange, message: BaseMessage) {
        defaultRabbitTemplate.convertAndSend(exchange.exchangeName, exchange.routingKey, message)
    }

}