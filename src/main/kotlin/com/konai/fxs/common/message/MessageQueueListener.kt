package com.konai.fxs.common.message

import com.konai.fxs.v1.transaction.service.V1TransactionExpireService
import com.konai.fxs.v1.transaction.service.V1TransactionSaveService
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class MessageQueueListener(
    private val v1TransactionSaveService: V1TransactionSaveService,
    private val v1TransactionExpireService: V1TransactionExpireService
) {
    // logger
    private val logger = LoggerFactory.getLogger(this::class.java)

    private fun logging(message: BaseMessage, queueName: String) {
        logger.info("[${message.correlationId}] [$queueName] Received.")
    }

    @RabbitListener(queues = [MessageQueue.V1_SAVE_TRANSACTION_QUEUE])
    fun receiveMessage(message: V1SaveTransactionMessage) {
        logging(message, MessageQueue.V1_SAVE_TRANSACTION_QUEUE)
        v1TransactionSaveService.save(message.transaction)
    }

    @RabbitListener(queues = [MessageQueue.V1_EXPIRE_PREPARED_TRANSACTION_DL_QUEUE])
    fun receiveMessage(message: V1ExpirePreparedTransactionMessage) {
        logging(message, MessageQueue.V1_EXPIRE_PREPARED_TRANSACTION_DL_QUEUE)
        v1TransactionExpireService.expirePreparedTransaction(message.transactionId, message.amount)
    }

}