package com.konai.fxs.v1.transaction.controller.model

import com.konai.fxs.common.Currency
import com.konai.fxs.common.enumerate.AcquirerType.FX_DEPOSIT
import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.enumerate.TransactionPurpose
import com.konai.fxs.testsupport.TestExtensionFunctions.fixtureMonkey
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate.V1AcquirerPredicate
import com.navercorp.fixturemonkey.kotlin.giveMeKotlinBuilder
import java.math.BigDecimal

class V1TransactionWithdrawalPrepareRequestFixture {

    fun make(
        acquirer: V1AcquirerPredicate = V1AcquirerPredicate(generateUUID(), FX_DEPOSIT, "외화 예치금 계좌"),
        trReferenceId: String = generateUUID(),
        purpose: TransactionPurpose = TransactionPurpose.WITHDRAWAL,
        channel: TransactionChannel = TransactionChannel.PORTAL,
        currency: String = Currency.USD,
        amount: BigDecimal = BigDecimal(100),
        exchangeRate: BigDecimal = BigDecimal(1000.0)
    ): V1TransactionWithdrawalPrepareRequest {
        return fixtureMonkey.giveMeKotlinBuilder<V1TransactionWithdrawalPrepareRequest>()
            .setExp(V1TransactionWithdrawalPrepareRequest::acquirer, acquirer)
            .setExp(V1TransactionWithdrawalPrepareRequest::trReferenceId, trReferenceId)
            .setExp(V1TransactionWithdrawalPrepareRequest::purpose, purpose)
            .setExp(V1TransactionWithdrawalPrepareRequest::channel, channel)
            .setExp(V1TransactionWithdrawalPrepareRequest::currency, currency)
            .setExp(V1TransactionWithdrawalPrepareRequest::amount, amount)
            .setExp(V1TransactionWithdrawalPrepareRequest::exchangeRate, exchangeRate)
            .sample()
    }

}