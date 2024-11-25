package com.konai.fxs.common.message

interface MessageQueuePublisher {

    fun sendDirectMessage(exchange: MessageQueueExchange, message: BaseMessage)

}