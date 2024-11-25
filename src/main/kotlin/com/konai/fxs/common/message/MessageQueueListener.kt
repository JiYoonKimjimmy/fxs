package com.konai.fxs.common.message

import com.konai.fxs.v1.transaction.service.V1TransactionSaveService
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class MessageQueueListener(
    private val v1TransactionSaveService: V1TransactionSaveService
) {
    // logger
    private val logger = LoggerFactory.getLogger(this::class.java)

    @RabbitListener(queues = [MessageQueue.V1_SAVE_TRANSACTION_QUEUE])
    fun receiveMessage(message: V1SaveTransactionMessage) {
        logger.info("[${message.correlationId}] [${MessageQueue.V1_SAVE_TRANSACTION_QUEUE}] Received.")
        v1TransactionSaveService.save(message.transaction)
    }

}