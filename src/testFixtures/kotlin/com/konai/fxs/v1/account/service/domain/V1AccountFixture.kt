package com.konai.fxs.v1.account.service.domain

import com.konai.fxs.common.Currency
import com.konai.fxs.common.enumerate.AccountStatus
import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.common.enumerate.AcquirerType.FX_DEPOSIT
import com.konai.fxs.testsupport.TestExtensionFunctions.fixtureMonkey
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import com.navercorp.fixturemonkey.kotlin.giveMeKotlinBuilder
import java.math.BigDecimal

class V1AccountFixture {

    fun make(
        id: Long? = null,
        acquirerId: String = generateUUID(),
        acquirerType: AcquirerType = FX_DEPOSIT,
        acquirerName: String = "외화 예치금 계좌",
        currency: String = Currency.USD,
        balance: Long = 0,
        quantity: Int = 0,
        status: AccountStatus = AccountStatus.ACTIVE
    ): V1Account {
        return fixtureMonkey.giveMeKotlinBuilder<V1Account>()
            .setExp(V1Account::id, id)
            .setExp(V1Account::acquirer, V1Acquirer(acquirerId, acquirerType, acquirerName))
            .setExp(V1Account::currency, currency)
            .setExp(V1Account::balance, BigDecimal(balance))
            .setExp(V1Account::minRequiredBalance, BigDecimal.ZERO)
            .setExp(V1Account::averageExchangeRate, BigDecimal.ZERO)
            .setExp(V1Account::depositAmount, BigDecimal(quantity))
            .setExp(V1Account::status, status)
            .sample()
    }

}