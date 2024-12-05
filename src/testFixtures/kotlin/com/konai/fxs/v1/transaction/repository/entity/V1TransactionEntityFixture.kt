package com.konai.fxs.v1.transaction.repository.entity

import com.konai.fxs.common.Currency
import com.konai.fxs.common.DEFAULT_REQUEST_BY
import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.enumerate.TransactionPurpose
import com.konai.fxs.common.enumerate.TransactionStatus
import com.konai.fxs.common.enumerate.TransactionType
import com.konai.fxs.common.util.convertPatternOf
import com.konai.fxs.testsupport.TestExtensionFunctions.generateAcquirerEntity
import com.konai.fxs.testsupport.TestExtensionFunctions.generateSequence
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import com.konai.fxs.v1.account.repository.entity.V1AccountEntity.V1AcquirerEntity
import java.math.BigDecimal
import java.time.LocalDateTime

class V1TransactionEntityFixture {

    fun make(
        id: Long = generateSequence(),
        trReferenceId: String = generateUUID(),
        acquirer: V1AcquirerEntity = generateAcquirerEntity(),
        fromAcquirer: V1AcquirerEntity = generateAcquirerEntity(),
        type: TransactionType = TransactionType.DEPOSIT,
        purpose: TransactionPurpose = TransactionPurpose.DEPOSIT,
        channel: TransactionChannel = TransactionChannel.PORTAL,
        currency: String = Currency.USD,
        amount: BigDecimal = BigDecimal(100),
        exchangeRate: BigDecimal = BigDecimal(1000),
        transferDate: String = LocalDateTime.now().convertPatternOf(),
        requestBy: String = DEFAULT_REQUEST_BY,
        requestNote: String? = null,
        status: TransactionStatus = TransactionStatus.CREATED,
        cancelDate: String? = null,
        orgTransactionId: Long? = null,
        orgTrReferenceId: String? = null,
    ): V1TransactionEntity {
        return V1TransactionEntity(
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
            status = status,
            cancelDate = cancelDate,
            orgTransactionId = orgTransactionId,
            orgTrReferenceId = orgTrReferenceId
        )
    }

}