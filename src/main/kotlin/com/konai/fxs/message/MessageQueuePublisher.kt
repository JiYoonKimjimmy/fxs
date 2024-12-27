package com.konai.fxs.message

interface MessageQueuePublisher {

    fun sendDirectMessage(exchange: MessageQueueExchange, message: BaseMessage)

}