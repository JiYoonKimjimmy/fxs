package com.konai.fxs.v1.transaction.service.domain

import com.konai.fxs.common.enumerate.TransactionStatus
import com.konai.fxs.common.enumerate.TransactionType
import com.konai.fxs.common.getCorrelationId
import com.konai.fxs.common.message.V1ExpireTransactionMessage
import com.konai.fxs.common.util.convertPatternOf
import com.konai.fxs.v1.account.repository.entity.V1AccountEntity.V1AcquirerEntity
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import com.konai.fxs.v1.transaction.controller.model.V1TransactionManualDepositRequest
import com.konai.fxs.v1.transaction.controller.model.V1TransactionManualWithdrawalRequest
import com.konai.fxs.v1.transaction.controller.model.V1TransactionWithdrawalRequest
import com.konai.fxs.v1.transaction.repository.entity.V1TransactionEntity
import com.konai.fxs.v1.transaction.service.event.V1ExpireTransactionEvent
import com.konai.fxs.v1.transaction.service.event.V1SaveTransactionEvent
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime

@Component
class V1TransactionMapper {

    fun requestToDomain(request: V1TransactionManualDepositRequest): V1Transaction {
        return V1Transaction(
            trReferenceId = getCorrelationId(),
            channel = request.channel,
            baseAcquirer = request.toAcquirer.toDomain(),
            targetAcquirer = request.fromAcquirer.toDomain(),
            type = TransactionType.DEPOSIT,
            purpose = request.purpose,
            currency = request.currency,
            amount = request.amount,
            beforeBalance = BigDecimal.ZERO,
            afterBalance = BigDecimal.ZERO,
            exchangeRate = request.exchangeRate,
            transferDate = request.transferDate,
            completeDate = null,
            cancelDate = null,
            orgTransactionId = null,
            orgTrReferenceId = null,
            requestBy = request.requestBy,
            requestNote = request.requestNote,
            status = TransactionStatus.CREATED,
        )
    }

    fun requestToDomain(request: V1TransactionManualWithdrawalRequest): V1Transaction {
        return V1Transaction(
            trReferenceId = getCorrelationId(),
            channel = request.channel,
            baseAcquirer = request.fromAcquirer.toDomain(),
            targetAcquirer = request.toAcquirer?.toDomain(),
            type = TransactionType.WITHDRAWAL,
            purpose = request.purpose,
            currency = request.currency,
            amount = request.amount,
            beforeBalance = BigDecimal.ZERO,
            afterBalance = BigDecimal.ZERO,
            exchangeRate = request.exchangeRate,
            transferDate = request.transferDate,
            completeDate = null,
            cancelDate = null,
            orgTransactionId = null,
            orgTrReferenceId = null,
            requestBy = request.requestBy,
            requestNote = request.requestNote,
            status = TransactionStatus.CREATED,
        )
    }

    fun requestToDomain(request: V1TransactionWithdrawalRequest): V1Transaction {
        return V1Transaction(
            trReferenceId = request.trReferenceId,
            channel = request.channel,
            baseAcquirer = request.acquirer.toDomain(),
            targetAcquirer = null,
            type = TransactionType.WITHDRAWAL,
            purpose = request.purpose,
            currency = request.currency,
            amount = request.amount,
            beforeBalance = BigDecimal.ZERO,
            afterBalance = BigDecimal.ZERO,
            exchangeRate = request.exchangeRate,
            transferDate = LocalDateTime.now().convertPatternOf(),
            completeDate = null,
            cancelDate = null,
            orgTransactionId = null,
            orgTrReferenceId = null,
            requestBy = request.channel.name,
            requestNote = request.channel.note,
            status = TransactionStatus.CREATED,
        )
    }

    fun domainToSaveTransactionEvent(domain: V1Transaction): V1SaveTransactionEvent {
        return V1SaveTransactionEvent(transaction = domain)
    }

    fun domainToExpireTransactionEvent(domain: V1Transaction): V1ExpireTransactionEvent {
        return V1ExpireTransactionEvent(transaction = domain)
    }

    fun eventToMessage(event: V1ExpireTransactionEvent): V1ExpireTransactionMessage {
        return V1ExpireTransactionMessage(
            transactionId = event.transaction.id!!,
            amount = event.transaction.amount.toLong()
        )
    }

    fun domainToEntity(domain: V1Transaction): V1TransactionEntity {
        return V1TransactionEntity(
            id = domain.id,
            trReferenceId = domain.trReferenceId,
            channel = domain.channel,
            baseAcquirer = V1AcquirerEntity(id = domain.baseAcquirer.id, type = domain.baseAcquirer.type, name = domain.baseAcquirer.name),
            targetAcquirer = domain.targetAcquirer?.let { V1AcquirerEntity(id = it.id, type = it.type, name = it.name) },
            type = domain.type,
            purpose = domain.purpose,
            currency = domain.currency,
            amount = domain.amount,
            beforeBalance = domain.beforeBalance,
            afterBalance = domain.afterBalance,
            exchangeRate = domain.exchangeRate,
            transferDate = domain.transferDate,
            completeDate = domain.completeDate ?: LocalDateTime.now(),
            cancelDate = domain.cancelDate,
            orgTransactionId = domain.orgTransactionId,
            orgTrReferenceId = domain.orgTrReferenceId,
            requestBy = domain.requestBy,
            requestNote = domain.requestNote,
            status = domain.status,
        )
    }

    fun entityToDomain(entity: V1TransactionEntity): V1Transaction {
        return V1Transaction(
            id = entity.id!!,
            trReferenceId = entity.trReferenceId,
            channel = entity.channel,
            baseAcquirer = V1Acquirer(id = entity.baseAcquirer.id, type = entity.baseAcquirer.type, name = entity.baseAcquirer.name),
            targetAcquirer = entity.targetAcquirer?.let { V1Acquirer(id = it.id, type = it.type, name = it.name) },
            type = entity.type,
            purpose = entity.purpose,
            currency = entity.currency,
            amount = entity.amount,
            beforeBalance = entity.beforeBalance,
            afterBalance = entity.afterBalance,
            exchangeRate = entity.exchangeRate,
            transferDate = entity.transferDate,
            completeDate = entity.completeDate,
            cancelDate = entity.cancelDate,
            orgTransactionId = entity.orgTransactionId,
            orgTrReferenceId = entity.orgTrReferenceId,
            requestBy = entity.requestBy,
            requestNote = entity.requestNote,
            status = entity.status,
        )
    }

}