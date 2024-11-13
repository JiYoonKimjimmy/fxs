package com.konai.fxs.v1.account.service.domain

import com.konai.fxs.v1.account.controller.model.*
import com.konai.fxs.v1.account.repository.entity.V1AccountEntity
import com.konai.fxs.v1.account.repository.entity.V1AccountEntity.V1AcquirerEntity
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class V1AccountMapper {

    fun requestToDomain(request: V1CreateAccountRequest): V1Account {
        return V1Account(
            acquirer = request.acquirer,
            currency = request.currency,
            minRequiredBalance = BigDecimal(request.minRequiredBalance),
            averageExchangeRate = BigDecimal.ZERO,
        )
    }

    fun requestToPredicate(request: V1FindOneAccountRequest): V1AccountPredicate {
        return V1AccountPredicate(
            id = request.accountId,
            acquirer = request.acquirer
        )
    }

    fun requestToPredicate(request: V1FindAllAccountRequest): V1AccountPredicate {
        return V1AccountPredicate(
            id = request.accountId,
            acquirer = request.acquirer,
            currency = request.currency,
        )
    }

    fun requestToPredicate(request: V1UpdateAccountRequest): V1AccountPredicate {
        return V1AccountPredicate(
            id = request.accountId,
            acquirer = request.acquirer,
            currency = request.currency,
            minRequiredBalance = request.minRequiredBalance?.let(BigDecimal::valueOf),
            status = request.status
        )
    }

    fun domainToModel(domain: V1Account): V1AccountModel {
        return V1AccountModel(
            accountId = domain.id,
            acquirerId = domain.acquirer.id,
            acquirerType = domain.acquirer.type,
            acquirerName = domain.acquirer.name,
            currency = domain.currency,
            balance = domain.balance.toLong(),
            minRequiredBalance = domain.minRequiredBalance.toLong(),
            averageExchangeRate = domain.averageExchangeRate.toDouble(),
            quantity = domain.depositQuantity.toInt(),
            status = domain.status
        )
    }

    fun domainToEntity(domain: V1Account): V1AccountEntity {
        return V1AccountEntity(
            id = domain.id,
            acquirer = V1AcquirerEntity(
                id = domain.acquirer.id,
                type = domain.acquirer.type,
                name = domain.acquirer.name,
            ),
            currency = domain.currency,
            balance = domain.balance,
            minRequiredBalance = domain.minRequiredBalance,
            averageExchangeRate = domain.averageExchangeRate,
            depositQuantity = domain.depositQuantity,
            status = domain.status
        )
    }

    fun entityToDomain(entity: V1AccountEntity): V1Account {
        return V1Account(
            id = entity.id,
            acquirer = V1Acquirer(
                id = entity.acquirer.id,
                type = entity.acquirer.type,
                name = entity.acquirer.name,
            ),
            currency = entity.currency,
            balance = entity.balance,
            minRequiredBalance = entity.minRequiredBalance,
            averageExchangeRate = entity.averageExchangeRate,
            depositQuantity = entity.depositQuantity,
            status = entity.status
        )
    }

}