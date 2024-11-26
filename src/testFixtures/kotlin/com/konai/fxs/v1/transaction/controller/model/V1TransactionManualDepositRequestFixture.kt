package com.konai.fxs.v1.transaction.controller.model

import com.konai.fxs.common.Currency
import com.konai.fxs.common.enumerate.AcquirerType.FX_DEPOSIT
import com.konai.fxs.common.enumerate.AcquirerType.FX_PURCHASER
import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.enumerate.TransactionPurpose
import com.konai.fxs.common.util.convertPatternOf
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate.V1AcquirerPredicate
import java.math.BigDecimal
import java.time.LocalDateTime

class V1TransactionManualDepositRequestFixture {

    fun make(
        fromAcquirer: V1AcquirerPredicate = V1AcquirerPredicate(generateUUID(), FX_PURCHASER, "외화 매입처 계좌"),
        toAcquirer: V1AcquirerPredicate = V1AcquirerPredicate(generateUUID(), FX_DEPOSIT, "외화 예치금 계좌"),
        purpose: TransactionPurpose = TransactionPurpose.DEPOSIT,
        channel: TransactionChannel = TransactionChannel.PORTAL,
        currency: String = Currency.USD,
        amount: BigDecimal = BigDecimal(100),
        exchangeRate: BigDecimal = BigDecimal(1000.0),
        transferDate: String = LocalDateTime.now().convertPatternOf(),
        requestBy: String = "FXS_TEST",
        requestNote: String? = "FXS_TEST",
    ): V1TransactionManualDepositRequest {
        return V1TransactionManualDepositRequest(
            fromAcquirer = fromAcquirer,
            toAcquirer = toAcquirer,
            purpose = purpose,
            channel = channel,
            currency = currency,
            amount = amount,
            exchangeRate = exchangeRate,
            transferDate = transferDate,
            requestBy = requestBy,
            requestNote = requestNote,
        )
    }

}