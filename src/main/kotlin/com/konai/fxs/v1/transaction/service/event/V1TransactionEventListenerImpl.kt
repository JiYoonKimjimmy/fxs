package com.konai.fxs.v1.transaction.service.event

import com.konai.fxs.v1.transaction.service.V1TransactionSaveService
import com.konai.fxs.v1.transaction.service.domain.V1TransactionMapper
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class V1TransactionEventListenerImpl(
    private val v1TransactionMapper: V1TransactionMapper,
    private val v1TransactionSaveService: V1TransactionSaveService
) : V1TransactionEventListener {
    // logger
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Async
    @EventListener
    override fun saveTransactionEventHandler(event: V1SaveTransactionEvent) {
        logger.info("[SaveTransactionEvent] EventListener Started")
        v1TransactionMapper.eventToDomain(event).let { v1TransactionSaveService.save(it) }
        logger.info("[SaveTransactionEvent] EventListener Completed")
    }

}