package com.konai.fxs.v1.transaction.service.domain

import com.konai.fxs.common.enumerate.TransactionStatus
import com.konai.fxs.common.enumerate.TransactionType
import com.konai.fxs.common.message.V1ExpirePreparedTransactionMessage
import com.konai.fxs.common.util.convertPatternOf
import com.konai.fxs.v1.account.repository.entity.V1AccountEntity.V1AcquirerEntity
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import com.konai.fxs.v1.transaction.controller.model.V1TransactionManualDepositRequest
import com.konai.fxs.v1.transaction.controller.model.V1TransactionManualWithdrawalRequest
import com.konai.fxs.v1.transaction.controller.model.V1TransactionWithdrawalRequest
import com.konai.fxs.v1.transaction.repository.entity.V1TransactionEntity
import com.konai.fxs.v1.transaction.service.event.V1ExpirePreparedTransactionEvent
import com.konai.fxs.v1.transaction.service.event.V1SaveTransactionEvent
import com.konasl.commonlib.springweb.correlation.core.ContextField
import com.konasl.commonlib.springweb.correlation.core.RequestContext
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class V1TransactionMapper {

    fun requestToDomain(request: V1TransactionManualDepositRequest): V1Transaction {
        return V1Transaction(
            trReferenceId = RequestContext.get(ContextField.CORRELATION_ID),
            acquirer = request.toAcquirer.toDomain(),
            fromAcquirer = request.fromAcquirer.toDomain(),
            type = TransactionType.DEPOSIT,
            purpose = request.purpose,
            channel = request.channel,
            currency = request.currency,
            amount = request.amount,
            exchangeRate = request.exchangeRate,
            transferDate = request.transferDate,
            requestBy = request.requestBy,
            requestNote = request.requestNote,
            status = TransactionStatus.CREATED
        )
    }

    fun requestToDomain(request: V1TransactionManualWithdrawalRequest): V1Transaction {
        return V1Transaction(
            trReferenceId = RequestContext.get(ContextField.CORRELATION_ID),
            acquirer = request.fromAcquirer.toDomain(),
            fromAcquirer = request.toAcquirer?.toDomain(),
            type = TransactionType.WITHDRAWAL,
            purpose = request.purpose,
            channel = request.channel,
            currency = request.currency,
            amount = request.amount,
            exchangeRate = request.exchangeRate,
            transferDate = request.transferDate,
            requestBy = request.requestBy,
            requestNote = request.requestNote,
            status = TransactionStatus.CREATED
        )
    }

    fun requestToDomain(request: V1TransactionWithdrawalRequest): V1Transaction {
        return V1Transaction(
            trReferenceId = request.trReferenceId,
            acquirer = request.acquirer.toDomain(),
            fromAcquirer = null,
            type = TransactionType.WITHDRAWAL,
            purpose = request.purpose,
            channel = request.channel,
            currency = request.currency,
            amount = request.amount,
            exchangeRate = request.exchangeRate,
            transferDate = LocalDateTime.now().convertPatternOf(),
            requestBy = request.channel.name,
            requestNote = request.channel.note,
            status = TransactionStatus.CREATED
        )
    }

    fun domainToSaveTransactionEvent(domain: V1Transaction): V1SaveTransactionEvent {
        return V1SaveTransactionEvent(transaction = domain)
    }

    fun domainToExpirePreparedTransactionEvent(domain: V1Transaction): V1ExpirePreparedTransactionEvent {
        return V1ExpirePreparedTransactionEvent(transaction = domain)
    }

    fun eventToDomain(event: V1SaveTransactionEvent): V1Transaction {
        return V1Transaction(
            id = event.transaction.id,
            trReferenceId = event.transaction.trReferenceId,
            acquirer = event.transaction.acquirer,
            fromAcquirer = event.transaction.fromAcquirer,
            type = event.transaction.type,
            purpose = event.transaction.purpose,
            channel = event.transaction.channel,
            currency = event.transaction.currency,
            amount = event.transaction.amount,
            exchangeRate = event.transaction.exchangeRate,
            transferDate = event.transaction.transferDate,
            requestBy = event.transaction.requestBy,
            requestNote = event.transaction.requestNote,
            status = event.transaction.status
        )
    }

    fun eventToMessage(event: V1ExpirePreparedTransactionEvent): V1ExpirePreparedTransactionMessage {
        return V1ExpirePreparedTransactionMessage(
            transactionId = event.transaction.id!!,
            amount = event.transaction.amount.toLong()
        )
    }

    fun domainToEntity(domain: V1Transaction): V1TransactionEntity {
        return V1TransactionEntity(
            id = domain.id,
            trReferenceId = domain.trReferenceId,
            acquirer = V1AcquirerEntity(id = domain.acquirer.id, type = domain.acquirer.type, name = domain.acquirer.name),
            fromAcquirer = domain.fromAcquirer?.let { V1AcquirerEntity(id = it.id, type = it.type, name = it.name) },
            type = domain.type,
            purpose = domain.purpose,
            channel = domain.channel,
            currency = domain.currency,
            amount = domain.amount,
            exchangeRate = domain.exchangeRate,
            transferDate = domain.transferDate,
            requestBy = domain.requestBy,
            requestNote = domain.requestNote,
            status = domain.status
        )
    }

    fun entityToDomain(entity: V1TransactionEntity): V1Transaction {
        return V1Transaction(
            id = entity.id!!,
            trReferenceId = entity.trReferenceId,
            acquirer = V1Acquirer(id = entity.acquirer.id, type = entity.acquirer.type, name = entity.acquirer.name),
            fromAcquirer = entity.fromAcquirer?.let { V1Acquirer(id = it.id, type = it.type, name = it.name) },
            type = entity.type,
            purpose = entity.purpose,
            channel = entity.channel,
            currency = entity.currency,
            amount = entity.amount,
            exchangeRate = entity.exchangeRate,
            transferDate = entity.transferDate,
            requestBy = entity.requestBy,
            requestNote = entity.requestNote,
            status = entity.status
        )
    }

}