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
        channel: TransactionChannel = TransactionChannel.PORTAL,
        baseAcquirer: V1Acquirer = V1Acquirer(generateUUID(), FX_DEPOSIT, "외화 예치금 계좌"),
        targetAcquirer: V1Acquirer? = V1Acquirer(generateUUID(), FX_DEPOSIT, "외화 매입처 계좌"),
        type: TransactionType = TransactionType.DEPOSIT,
        purpose: TransactionPurpose = TransactionPurpose.DEPOSIT,
        currency: String = Currency.USD,
        amount: BigDecimal = BigDecimal(100),
        beforeBalance: BigDecimal = BigDecimal.ZERO,
        afterBalance: BigDecimal = BigDecimal.ZERO,
        exchangeRate: BigDecimal = BigDecimal(1000),
        transferDate: String = LocalDateTime.now().convertPatternOf(),
        completeDate: LocalDateTime? = LocalDateTime.now(),
        cancelDate: LocalDateTime? = null,
        orgTransactionId: Long? = null,
        orgTrReferenceId: String? = null,
        requestBy: String = DEFAULT_REQUEST_BY,
        requestNote: String? = null,
        status: TransactionStatus = TransactionStatus.CREATED,
    ): V1Transaction {
        return V1Transaction(
            id = id,
            trReferenceId = trReferenceId,
            channel = channel,
            baseAcquirer = baseAcquirer,
            targetAcquirer = targetAcquirer,
            type = type,
            purpose = purpose,
            currency = currency,
            amount = amount,
            beforeBalance = beforeBalance,
            afterBalance = afterBalance,
            exchangeRate = exchangeRate,
            transferDate = transferDate,
            completeDate = completeDate,
            cancelDate = cancelDate,
            orgTransactionId = orgTransactionId,
            orgTrReferenceId = orgTrReferenceId,
            requestBy = requestBy,
            requestNote = requestNote,
            status = status,
        )
    }

    fun manualDepositTransaction(
        baseAcquirer: V1Acquirer,
        targetAcquirer: V1Acquirer,
        amount: BigDecimal,
        exchangeRate: BigDecimal
    ): V1Transaction {
        return make(
            channel = TransactionChannel.PORTAL,
            baseAcquirer = baseAcquirer,
            targetAcquirer = targetAcquirer,
            type = TransactionType.DEPOSIT,
            purpose = TransactionPurpose.DEPOSIT,
            amount = amount,
            exchangeRate = exchangeRate
        )
    }

    fun manualWithdrawalTransaction(
        baseAcquirer: V1Acquirer,
        targetAcquirer: V1Acquirer? = null,
        purpose: TransactionPurpose = TransactionPurpose.WITHDRAWAL,
        amount: BigDecimal,
        exchangeRate: BigDecimal
    ): V1Transaction {
        return make(
            channel = TransactionChannel.PORTAL,
            baseAcquirer = baseAcquirer,
            targetAcquirer = targetAcquirer,
            type = TransactionType.WITHDRAWAL,
            purpose = purpose,
            amount = amount,
            exchangeRate = exchangeRate
        )
    }

    fun withdrawalTransaction(
        baseAcquirer: V1Acquirer,
        trReferenceId: String = generateUUID(),
        amount: BigDecimal
    ): V1Transaction {
        return make(
            channel = TransactionChannel.ORS,
            baseAcquirer = baseAcquirer,
            targetAcquirer = null,
            trReferenceId = trReferenceId,
            type = TransactionType.WITHDRAWAL,
            purpose = TransactionPurpose.REMITTANCE,
            amount = amount
        )
    }

}