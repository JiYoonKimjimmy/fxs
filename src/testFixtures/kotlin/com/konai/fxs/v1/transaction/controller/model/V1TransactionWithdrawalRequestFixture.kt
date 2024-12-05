package com.konai.fxs.v1.transaction.controller.model

import com.konai.fxs.common.Currency
import com.konai.fxs.common.enumerate.AcquirerType.FX_DEPOSIT
import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.enumerate.TransactionPurpose
import com.konai.fxs.testsupport.TestExtensionFunctions.fixtureMonkey
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import com.konai.fxs.v1.account.controller.model.V1AcquirerModel
import com.navercorp.fixturemonkey.kotlin.giveMeKotlinBuilder
import java.math.BigDecimal

class V1TransactionWithdrawalRequestFixture {

    fun make(
        acquirer: V1AcquirerModel = V1AcquirerModel(generateUUID(), FX_DEPOSIT, "외화 예치금 계좌"),
        trReferenceId: String = generateUUID(),
        purpose: TransactionPurpose = TransactionPurpose.WITHDRAWAL,
        channel: TransactionChannel = TransactionChannel.PORTAL,
        currency: String = Currency.USD,
        amount: BigDecimal = BigDecimal(100),
        exchangeRate: BigDecimal = BigDecimal(1000.0)
    ): V1TransactionWithdrawalRequest {
        return fixtureMonkey.giveMeKotlinBuilder<V1TransactionWithdrawalRequest>()
            .setExp(V1TransactionWithdrawalRequest::acquirer, acquirer)
            .setExp(V1TransactionWithdrawalRequest::trReferenceId, trReferenceId)
            .setExp(V1TransactionWithdrawalRequest::purpose, purpose)
            .setExp(V1TransactionWithdrawalRequest::channel, channel)
            .setExp(V1TransactionWithdrawalRequest::currency, currency)
            .setExp(V1TransactionWithdrawalRequest::amount, amount)
            .setExp(V1TransactionWithdrawalRequest::exchangeRate, exchangeRate)
            .sample()
    }

}