package com.konai.fxs.v1.transaction.service.event

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class V1TransactionEventListenerImpl(
    private val v1TransactionEventService: V1TransactionEventService
) : V1TransactionEventListener {
    // logger
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Async
    @EventListener
    override fun saveTransactionEventHandler(event: V1SaveTransactionEvent) {
        logger.info("[SaveTransactionEvent] EventListener Started.")
        v1TransactionEventService.saveTransaction(event)
        logger.info("[SaveTransactionEvent] EventListener Completed.")
    }

    @Async
    @TransactionalEventListener
    override fun expireTransactionEventHandler(event: V1ExpireTransactionEvent) {
        logger.info("[ExpireTransactionEventHandler] EventListener Started.")
        v1TransactionEventService.expireTransaction(event)
        logger.info("[ExpireTransactionEventHandler] EventListener Completed.")
    }

    @Async
    @TransactionalEventListener
    override fun reverseTransactionEventHandler(event: V1ReverseTransactionEvent) {
        logger.info("[ReverseTransactionEventHandler] EventListener Started.")
        v1TransactionEventService.reverseTransaction(event)
        logger.info("[ReverseTransactionEventHandler] EventListener Completed.")
    }

}