package com.konai.fxs.v1.transaction.controller.model

import com.konai.fxs.common.Currency
import com.konai.fxs.common.enumerate.AcquirerType.FX_DEPOSIT
import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.enumerate.TransactionPurpose
import com.konai.fxs.common.util.convertPatternOf
import com.konai.fxs.testsupport.TestExtensionFunctions.fixtureMonkey
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate.V1AcquirerPredicate
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
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
        return fixtureMonkey.giveMeBuilder<V1TransactionManualWithdrawalRequest>()
            .set("fromAcquirer", fromAcquirer)
            .set("toAcquirer", toAcquirer)
            .set("purpose", purpose)
            .set("channel", channel)
            .set("currency", currency)
            .set("amount", amount)
            .set("exchangeRate", exchangeRate)
            .set("transferDate", transferDate)
            .set("requestBy", requestBy)
            .set("requestNote", requestNote)
            .sample()
    }

}