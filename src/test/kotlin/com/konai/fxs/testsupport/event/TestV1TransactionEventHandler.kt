package com.konai.fxs.testsupport.event

import com.konai.fxs.v1.transaction.repository.V1TransactionRepository
import com.konai.fxs.v1.transaction.service.domain.V1TransactionMapper
import com.konai.fxs.v1.transaction.service.event.V1SaveTransactionEvent
import com.konai.fxs.v1.transaction.service.event.V1TransactionEventListener
import org.springframework.context.ApplicationEventPublisher

class TestV1TransactionEventHandler(
    private val v1TransactionMapper: V1TransactionMapper,
    private val v1TransactionRepository: V1TransactionRepository
) : ApplicationEventPublisher, V1TransactionEventListener {

    override fun publishEvent(event: Any) {
        saveTransactionEventHandler(event as V1SaveTransactionEvent)
    }

    override fun saveTransactionEventHandler(event: V1SaveTransactionEvent) {
        v1TransactionMapper.eventToDomain(event)
            .let { v1TransactionRepository.save(it) }
    }

}