package com.konai.fxs.v1.transaction.service.event

import com.konai.fxs.v1.transaction.repository.V1TransactionRepository
import com.konai.fxs.v1.transaction.service.domain.V1TransactionMapper
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class V1TransactionEventListenerImpl(
    private val v1TransactionMapper: V1TransactionMapper,
    private val v1TransactionRepository: V1TransactionRepository
) : V1TransactionEventListener {
    // logger
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Async
    @EventListener
    override fun saveTransactionEventHandler(event: V1SaveTransactionEvent) {
        logger.info("SaveTransactionEvent Started!!")
        try {
            v1TransactionMapper.eventToDomain(event)
                .let { v1TransactionRepository.save(it) }
        } catch (e: Exception) {
            // TODO DBMS 시스템 장애 발생 시, 재시도 처리 필요
            logger.error("SaveTransactionEvent Error!!")
            logger.error(e.stackTraceToString())
        }
        logger.info("SaveTransactionEvent Completed!!")
    }

}