package com.konai.fxs.v1.transaction.service.domain

import com.konai.fxs.common.Currency
import com.konai.fxs.common.DEFAULT_REQUEST_BY
import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.enumerate.TransactionPurpose
import com.konai.fxs.common.enumerate.TransactionType
import com.konai.fxs.common.util.convertPatternOf
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import java.math.BigDecimal
import java.time.LocalDateTime

class V1TransactionFixture {

    private fun make(
        acquirer: V1Acquirer,
        fromAcquirer: V1Acquirer,
        type: TransactionType,
        purpose: TransactionPurpose,
        channel: TransactionChannel,
        currency: String = Currency.USD,
        amount: BigDecimal,
        exchangeRate: BigDecimal,
        transferDate: String = LocalDateTime.now().convertPatternOf(),
        requestBy: String = DEFAULT_REQUEST_BY,
        requestNote: String? = null,
    ): V1Transaction {
        return V1Transaction(
            acquirer = acquirer,
            fromAcquirer = fromAcquirer,
            type = type,
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

    fun manualDepositTransaction(
        acquirer: V1Acquirer,
        fromAcquirer: V1Acquirer,
        amount: BigDecimal,
        exchangeRate: BigDecimal
    ): V1Transaction {
        return make(
            acquirer = acquirer,
            fromAcquirer = fromAcquirer,
            type = TransactionType.DEPOSIT,
            purpose = TransactionPurpose.DEPOSIT,
            channel = TransactionChannel.PORTAL,
            amount = amount,
            exchangeRate = exchangeRate
        )
    }

}