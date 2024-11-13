package com.konai.fxs.v1.transaction.service.event

import com.konai.fxs.v1.transaction.service.domain.V1Transaction
import com.konai.fxs.v1.transaction.service.domain.V1TransactionMapper
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class V1TransactionEventPublisherImpl(
    private val v1TransactionMapper: V1TransactionMapper,
    private val eventPublisher: ApplicationEventPublisher
) : V1TransactionEventPublisher {

    override fun saveTransaction(transaction: V1Transaction) {
        v1TransactionMapper.domainToSaveTransactionEvent(transaction)
            .let { eventPublisher.publishEvent(it) }
    }

}