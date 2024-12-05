package com.konai.fxs.v1.transaction.service.event

import com.konai.fxs.common.message.MessageQueueExchange.V1_EXPIRE_TRANSACTION_EXCHANGE
import com.konai.fxs.common.message.MessageQueuePublisher
import com.konai.fxs.v1.transaction.service.V1TransactionSaveService
import com.konai.fxs.v1.transaction.service.domain.V1TransactionMapper
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class V1TransactionEventListenerImpl(
    private val v1TransactionMapper: V1TransactionMapper,
    private val v1TransactionSaveService: V1TransactionSaveService,
    private val v1TransactionEventPublisher: V1TransactionEventPublisher,
    private val messageQueuePublisher: MessageQueuePublisher,
) : V1TransactionEventListener {
    // logger
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Async
    @EventListener
    override fun saveTransactionEventHandler(event: V1SaveTransactionEvent) {
        logger.info("[SaveTransactionEvent] EventListener Started.")
        v1TransactionMapper.eventToDomain(event)
            .let { v1TransactionSaveService.save(it) }
            .let { v1TransactionEventPublisher.expireTransaction(it) }
        logger.info("[SaveTransactionEvent] EventListener Completed.")
    }

    @TransactionalEventListener
    override fun expireTransactionEventHandler(event: V1ExpireTransactionEvent) {
        logger.info("[ExpireTransactionEventHandler] EventListener Started.")
        v1TransactionMapper.eventToMessage(event)
            .let { messageQueuePublisher.sendDirectMessage(V1_EXPIRE_TRANSACTION_EXCHANGE, it) }
        logger.info("[ExpireTransactionEventHandler] EventListener Completed.")
    }

}