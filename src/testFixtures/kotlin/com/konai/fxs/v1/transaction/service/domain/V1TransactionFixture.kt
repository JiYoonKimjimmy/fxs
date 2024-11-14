package com.konai.fxs.v1.transaction.service.domain

import com.konai.fxs.common.Currency
import com.konai.fxs.common.DEFAULT_REQUEST_BY
import com.konai.fxs.common.enumerate.AcquirerType.FX_DEPOSIT
import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.enumerate.TransactionPurpose
import com.konai.fxs.common.enumerate.TransactionStatus
import com.konai.fxs.common.enumerate.TransactionType
import com.konai.fxs.common.util.convertPatternOf
import com.konai.fxs.testsupport.TestExtensionFunctions.generateSequence
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import java.math.BigDecimal
import java.time.LocalDateTime

class V1TransactionFixture {

    fun make(
        id: Long = generateSequence(),
        trReferenceId: String = generateUUID(),
        acquirer: V1Acquirer = V1Acquirer(generateUUID(), FX_DEPOSIT, "외화 예치금 계좌"),
        fromAcquirer: V1Acquirer = V1Acquirer(generateUUID(), FX_DEPOSIT, "외화 매입처 계좌"),
        type: TransactionType = TransactionType.DEPOSIT,
        purpose: TransactionPurpose = TransactionPurpose.DEPOSIT,
        channel: TransactionChannel = TransactionChannel.PORTAL,
        currency: String = Currency.USD,
        amount: BigDecimal = BigDecimal(100),
        exchangeRate: BigDecimal = BigDecimal(1000),
        transferDate: String = LocalDateTime.now().convertPatternOf(),
        requestBy: String = DEFAULT_REQUEST_BY,
        requestNote: String? = null,
        status: TransactionStatus = TransactionStatus.CREATED
    ): V1Transaction {
        return V1Transaction(
            id = id,
            trReferenceId = trReferenceId,
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
            status = status
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