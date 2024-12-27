package com.konai.fxs.v1.transaction.service.event

import com.konai.fxs.message.MessageQueueExchange.V1_EXPIRE_TRANSACTION_EXCHANGE
import com.konai.fxs.message.MessageQueuePublisher
import com.konai.fxs.v1.transaction.service.V1AccountTransactionService
import com.konai.fxs.v1.transaction.service.V1TransactionSaveService
import com.konai.fxs.v1.transaction.service.domain.V1TransactionMapper
import org.springframework.stereotype.Service

@Service
class V1TransactionEventServiceImpl(
    private val v1TransactionMapper: V1TransactionMapper,
    private val v1TransactionSaveService: V1TransactionSaveService,
    private val v1TransactionEventPublisher: V1TransactionEventPublisher,
    private val v1AccountTransactionService: V1AccountTransactionService,
    private val messageQueuePublisher: MessageQueuePublisher,
) : V1TransactionEventService {

    override fun saveTransaction(event: V1SaveTransactionEvent) {
        event.transaction
            .let(v1TransactionSaveService::save)
            .isNeedsExpireTransaction(v1TransactionEventPublisher::expireTransaction)
            .isNeedsReverseTransaction(v1TransactionEventPublisher::reverseTransaction)
    }

    override fun expireTransaction(event: V1ExpireTransactionEvent) {
        val message = v1TransactionMapper.eventToMessage(event)
        messageQueuePublisher.sendDirectMessage(V1_EXPIRE_TRANSACTION_EXCHANGE, message)
    }

    override fun reverseTransaction(event: V1ReverseTransactionEvent) {
        v1AccountTransactionService.reverse(event.transaction)
    }

}