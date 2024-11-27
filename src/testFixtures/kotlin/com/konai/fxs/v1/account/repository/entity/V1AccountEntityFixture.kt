package com.konai.fxs.v1.account.repository.entity

import com.konai.fxs.common.Currency
import com.konai.fxs.common.enumerate.AccountStatus
import com.konai.fxs.testsupport.TestExtensionFunctions.fixtureMonkey
import com.konai.fxs.testsupport.TestExtensionFunctions.generateAcquirerEntity
import com.konai.fxs.v1.account.repository.entity.V1AccountEntity.V1AcquirerEntity
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
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
        return fixtureMonkey.giveMeBuilder<V1AccountEntity>()
            .set("id", id)
            .set("acquirer", acquirer)
            .set("currency", currency)
            .set("balance", BigDecimal(balance))
            .set("minRequiredBalance", minRequiredBalance)
            .set("averageExchangeRate", averageExchangeRate)
            .set("depositAmount", quantity)
            .set("status", status)
            .sample()
    }

}