package com.konai.fxs.v1.transaction.service.domain

import com.konai.fxs.v1.account.repository.entity.V1AccountEntity.V1AcquirerEntity
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import com.konai.fxs.v1.transaction.repository.entity.V1TransactionEntity
import com.konai.fxs.v1.transaction.service.event.V1SaveTransactionEvent
import org.springframework.stereotype.Component

@Component
class V1TransactionMapper {

    fun domainToSaveTransactionEvent(domain: V1Transaction): V1SaveTransactionEvent {
        return V1SaveTransactionEvent(
            id = domain.id,
            trReferenceId = domain.trReferenceId,
            acquirer = domain.acquirer,
            fromAcquirer = domain.fromAcquirer,
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

    fun eventToDomain(event: V1SaveTransactionEvent): V1Transaction {
        return V1Transaction(
            id = event.id,
            trReferenceId = event.trReferenceId,
            acquirer = event.acquirer,
            fromAcquirer = event.fromAcquirer,
            type = event.type,
            purpose = event.purpose,
            channel = event.channel,
            currency = event.currency,
            amount = event.amount,
            exchangeRate = event.exchangeRate,
            transferDate = event.transferDate,
            requestBy = event.requestBy,
            requestNote = event.requestNote,
            status = event.status
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