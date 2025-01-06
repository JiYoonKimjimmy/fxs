package com.konai.fxs.message

import com.konai.fxs.v1.exchangerate.koreaexim.service.V1KoreaeximExchangeRateCollectService
import com.konai.fxs.v1.transaction.service.V1TransactionExpireService
import com.konai.fxs.v1.transaction.service.V1TransactionSaveService
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class MessageQueueListener(
    private val v1TransactionSaveService: V1TransactionSaveService,
    private val v1TransactionExpireService: V1TransactionExpireService,
    private val v1KoreaeximExchangeRateCollectService: V1KoreaeximExchangeRateCollectService
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

    @RabbitListener(queues = [MessageQueue.V1_EXPIRE_TRANSACTION_DL_QUEUE])
    fun receiveMessage(message: V1ExpireTransactionMessage) {
        logging(message, MessageQueue.V1_EXPIRE_TRANSACTION_DL_QUEUE)
        v1TransactionExpireService.expireTransaction(message.transactionId, message.amount)
    }

    @RabbitListener(queues = [MessageQueue.V1_EXCHANGE_RATE_COLLECTOR_TIMER_DL_QUEUE])
    fun receiveMessage(message: V1ExchangeRateCollectorTimerMessage) {
        logging(message, MessageQueue.V1_EXCHANGE_RATE_COLLECTOR_TIMER_DL_QUEUE)
        v1KoreaeximExchangeRateCollectService.collect(message.index, message.date)
    }

}