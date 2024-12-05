package com.konai.fxs.testsupport.event

import com.konai.fxs.common.enumerate.TransactionStatus.PENDING
import com.konai.fxs.common.message.MessageQueueExchange.V1_EXPIRE_PREPARED_TRANSACTION_EXCHANGE
import com.konai.fxs.common.message.MessageQueuePublisher
import com.konai.fxs.v1.transaction.service.V1TransactionSaveService
import com.konai.fxs.v1.transaction.service.domain.V1TransactionMapper
import com.konai.fxs.v1.transaction.service.event.V1ExpirePreparedTransactionEvent
import com.konai.fxs.v1.transaction.service.event.V1SaveTransactionEvent
import com.konai.fxs.v1.transaction.service.event.V1TransactionEventListener
import org.springframework.context.ApplicationEventPublisher

class TestV1TransactionEventHandler(
    private val v1TransactionMapper: V1TransactionMapper,
    private val v1TransactionSaveService: V1TransactionSaveService,
    private val messageQueuePublisher: MessageQueuePublisher
) : ApplicationEventPublisher, V1TransactionEventListener {

    override fun publishEvent(event: Any) {
        when(event) {
            is V1SaveTransactionEvent -> saveTransactionEventHandler(event)
            is V1ExpirePreparedTransactionEvent -> expirePreparedTransactionEventHandler(event)
        }
    }

    override fun saveTransactionEventHandler(event: V1SaveTransactionEvent) {
        v1TransactionMapper.eventToDomain(event)
            .let { v1TransactionSaveService.save(it) }
    }

    override fun expirePreparedTransactionEventHandler(event: V1ExpirePreparedTransactionEvent) {
        if (event.transaction.status == PENDING) {
            v1TransactionMapper.eventToMessage(event)
                .let { messageQueuePublisher.sendDirectMessage(V1_EXPIRE_PREPARED_TRANSACTION_EXCHANGE, it) }
        }
    }

}