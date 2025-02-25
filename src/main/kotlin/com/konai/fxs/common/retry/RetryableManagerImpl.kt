package com.konai.fxs.common.retry

import com.konai.fxs.message.MessageQueueExchange.*
import com.konai.fxs.common.error
import com.konai.fxs.message.MessageQueuePublisher
import com.konai.fxs.message.V1SaveTransactionMessage
import com.konai.fxs.infra.error.exception.BaseException
import com.konai.fxs.v1.transaction.service.domain.V1Transaction
import org.slf4j.LoggerFactory
import org.springframework.dao.ConcurrencyFailureException
import org.springframework.dao.DataAccessResourceFailureException
import org.springframework.dao.TransientDataAccessException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

@Component
class RetryableManagerImpl(
    private val messageQueuePublisher: MessageQueuePublisher
) : RetryableManager {
    // logger
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Retryable(
        retryFor = [
            DataAccessResourceFailureException::class,
            TransientDataAccessException::class,
            ConcurrencyFailureException::class,
            BaseException::class
        ],
        maxAttempts = 3,
        backoff = Backoff(delay = 500, multiplier = 2.0)
    )
    override fun retrySaveTransaction(transaction: V1Transaction, block: (V1Transaction) -> V1Transaction): V1Transaction {
        return block(transaction)
    }

    @Recover
    fun recoverSaveTransaction(exception: Exception, transaction: V1Transaction, block: (V1Transaction) -> V1Transaction): V1Transaction {
        logger.error(exception)
        messageQueuePublisher.sendDirectMessage(V1_SAVE_TRANSACTION_EXCHANGE, V1SaveTransactionMessage(transaction))
        return transaction
    }

}