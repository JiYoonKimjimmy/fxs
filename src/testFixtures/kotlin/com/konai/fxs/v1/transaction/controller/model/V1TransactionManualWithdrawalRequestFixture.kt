package com.konai.fxs.v1.transaction.controller.model

import com.konai.fxs.common.Currency
import com.konai.fxs.common.enumerate.AcquirerType.FX_DEPOSIT
import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.enumerate.TransactionPurpose
import com.konai.fxs.common.util.convertPatternOf
import com.konai.fxs.testsupport.TestExtensionFunctions.fixtureMonkey
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate.V1AcquirerPredicate
import com.navercorp.fixturemonkey.kotlin.giveMeKotlinBuilder
import java.math.BigDecimal
import java.time.LocalDateTime

class V1TransactionManualWithdrawalRequestFixture {

    fun make(
        fromAcquirer: V1AcquirerPredicate = V1AcquirerPredicate(generateUUID(), FX_DEPOSIT, "외화 예치금 계좌"),
        toAcquirer: V1AcquirerPredicate? = null,
        purpose: TransactionPurpose = TransactionPurpose.WITHDRAWAL,
        channel: TransactionChannel = TransactionChannel.PORTAL,
        currency: String = Currency.USD,
        amount: BigDecimal = BigDecimal(100),
        exchangeRate: BigDecimal = BigDecimal(1000.0),
        transferDate: String = LocalDateTime.now().convertPatternOf(),
        requestBy: String = "FXS_TEST",
        requestNote: String? = "FXS_TEST",
    ): V1TransactionManualWithdrawalRequest {
        return fixtureMonkey.giveMeKotlinBuilder<V1TransactionManualWithdrawalRequest>()
            .setExp(V1TransactionManualWithdrawalRequest::fromAcquirer, fromAcquirer)
            .setExp(V1TransactionManualWithdrawalRequest::toAcquirer, toAcquirer)
            .setExp(V1TransactionManualWithdrawalRequest::purpose, purpose)
            .setExp(V1TransactionManualWithdrawalRequest::channel, channel)
            .setExp(V1TransactionManualWithdrawalRequest::currency, currency)
            .setExp(V1TransactionManualWithdrawalRequest::amount, amount)
            .setExp(V1TransactionManualWithdrawalRequest::exchangeRate, exchangeRate)
            .setExp(V1TransactionManualWithdrawalRequest::transferDate, transferDate)
            .setExp(V1TransactionManualWithdrawalRequest::requestBy, requestBy)
            .setExp(V1TransactionManualWithdrawalRequest::requestNote, requestNote)
            .sample()
    }

}