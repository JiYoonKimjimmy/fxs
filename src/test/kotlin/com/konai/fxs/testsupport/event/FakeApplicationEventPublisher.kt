package com.konai.fxs.testsupport.event

import com.konai.fxs.testsupport.TestDependencies
import com.konai.fxs.v1.transaction.service.event.V1ExpireTransactionEvent
import com.konai.fxs.v1.transaction.service.event.V1ReverseTransactionEvent
import com.konai.fxs.v1.transaction.service.event.V1SaveTransactionEvent
import org.springframework.context.ApplicationEventPublisher

class FakeApplicationEventPublisher : ApplicationEventPublisher {

    private val v1TransactionEventService by lazy { TestDependencies.v1TransactionEventService }

    override fun publishEvent(event: Any) {
        when (event) {
            is V1SaveTransactionEvent -> v1TransactionEventService.saveTransaction(event)
            is V1ExpireTransactionEvent -> v1TransactionEventService.expireTransaction(event)
            is V1ReverseTransactionEvent -> v1TransactionEventService.reverseTransaction(event)
        }
    }

}