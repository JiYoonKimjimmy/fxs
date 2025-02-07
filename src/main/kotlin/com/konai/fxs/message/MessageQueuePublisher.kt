package com.konai.fxs.message

import org.springframework.amqp.core.MessagePostProcessor

interface MessageQueuePublisher {

    fun sendDirectMessage(exchange: MessageQueueExchange, message: BaseMessage)

    fun sendDirectMessage(exchange: MessageQueueExchange, message: BaseMessage, messagePostProcessor: MessagePostProcessor)

}