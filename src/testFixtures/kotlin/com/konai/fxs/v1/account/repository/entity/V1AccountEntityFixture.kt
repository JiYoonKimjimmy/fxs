package com.konai.fxs.v1.account.repository.entity

import com.konai.fxs.common.Currency
import com.konai.fxs.common.enumerate.AccountStatus
import com.konai.fxs.testsupport.TestExtensionFunctions.fixtureMonkey
import com.konai.fxs.testsupport.TestExtensionFunctions.generateAcquirerEntity
import com.konai.fxs.v1.account.repository.entity.V1AccountEntity.V1AcquirerEntity
import com.navercorp.fixturemonkey.kotlin.giveMeKotlinBuilder
import java.math.BigDecimal

class V1AccountEntityFixture {

    fun make(
        id: Long? = null,
        acquirer: V1AcquirerEntity = generateAcquirerEntity(),
        currency: String = Currency.USD,
        balance: Long = 0,
        minRequiredBalance: BigDecimal = BigDecimal.ZERO,
        averageExchangeRate: BigDecimal = BigDecimal.ZERO,
        quantity: BigDecimal = BigDecimal.ZERO,
        status: AccountStatus = AccountStatus.ACTIVE,
    ): V1AccountEntity {
        return fixtureMonkey.giveMeKotlinBuilder<V1AccountEntity>()
            .setExp(V1AccountEntity::id, id)
            .setExp(V1AccountEntity::acquirer, acquirer)
            .setExp(V1AccountEntity::currency, currency)
            .setExp(V1AccountEntity::balance, BigDecimal(balance))
            .setExp(V1AccountEntity::minRequiredBalance, minRequiredBalance)
            .setExp(V1AccountEntity::averageExchangeRate, averageExchangeRate)
            .setExp(V1AccountEntity::depositAmount, quantity)
            .setExp(V1AccountEntity::status, status)
            .sample()
    }

}