package com.konai.fxs.v1.transaction.controller.model

import com.konai.fxs.common.Currency
import com.konai.fxs.common.enumerate.AcquirerType.FX_DEPOSIT
import com.konai.fxs.common.enumerate.AcquirerType.FX_PURCHASER
import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.enumerate.TransactionPurpose
import com.konai.fxs.common.util.convertPatternOf
import com.konai.fxs.testsupport.TestExtensionFunctions.fixtureMonkey
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import com.konai.fxs.v1.account.controller.model.V1AcquirerModel
import com.navercorp.fixturemonkey.kotlin.giveMeKotlinBuilder
import java.math.BigDecimal
import java.time.LocalDateTime

class V1TransactionManualDepositRequestFixture {

    fun make(
        fromAcquirer: V1AcquirerModel = V1AcquirerModel(generateUUID(), FX_PURCHASER, "외화 매입처 계좌"),
        toAcquirer: V1AcquirerModel = V1AcquirerModel(generateUUID(), FX_DEPOSIT, "외화 예치금 계좌"),
        purpose: TransactionPurpose = TransactionPurpose.DEPOSIT,
        channel: TransactionChannel = TransactionChannel.PORTAL,
        currency: String = Currency.USD,
        amount: BigDecimal = BigDecimal(100),
        exchangeRate: BigDecimal = BigDecimal(1000.0),
        transferDate: String = LocalDateTime.now().convertPatternOf(),
        requestBy: String = "FXS_TEST",
        requestNote: String? = "FXS_TEST",
    ): V1TransactionManualDepositRequest {
        return fixtureMonkey.giveMeKotlinBuilder<V1TransactionManualDepositRequest>()
            .setExp(V1TransactionManualDepositRequest::fromAcquirer, fromAcquirer)
            .setExp(V1TransactionManualDepositRequest::toAcquirer, toAcquirer)
            .setExp(V1TransactionManualDepositRequest::purpose, purpose)
            .setExp(V1TransactionManualDepositRequest::channel, channel)
            .setExp(V1TransactionManualDepositRequest::currency, currency)
            .setExp(V1TransactionManualDepositRequest::amount, amount)
            .setExp(V1TransactionManualDepositRequest::exchangeRate, exchangeRate)
            .setExp(V1TransactionManualDepositRequest::transferDate, transferDate)
            .setExp(V1TransactionManualDepositRequest::requestBy, requestBy)
            .setExp(V1TransactionManualDepositRequest::requestNote, requestNote)
            .sample()
    }

}