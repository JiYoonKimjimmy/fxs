package com.konai.fxs.v1.transaction.service.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.konai.fxs.common.enumerate.*
import com.konai.fxs.common.enumerate.AcquirerType.FX_DEPOSIT
import com.konai.fxs.common.enumerate.AcquirerType.MTO_FUNDING
import com.konai.fxs.common.enumerate.TransactionPurpose.REMITTANCE
import com.konai.fxs.common.enumerate.TransactionStatus.*
import com.konai.fxs.common.enumerate.TransactionType.WITHDRAWAL
import com.konai.fxs.common.util.convertPatternOf
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import java.math.BigDecimal
import java.time.LocalDateTime

data class V1Transaction(
    var id: Long? = null,
    val trReferenceId: String,
    val channel: TransactionChannel,
    val baseAcquirer: V1Acquirer,
    val targetAcquirer: V1Acquirer?,
    val type: TransactionType,
    val purpose: TransactionPurpose,
    val currency: String,
    val amount: BigDecimal,
    val beforeBalance: BigDecimal,
    val afterBalance: BigDecimal,
    val exchangeRate: BigDecimal,
    val transferDate: String,
    var completeDate: LocalDateTime?,
    var cancelDate: LocalDateTime?,
    val orgTransactionId: Long?,
    val orgTrReferenceId: String?,
    val requestBy: String,
    val requestNote: String?,
    var status: TransactionStatus
) {
    @get:JsonIgnore
    lateinit var account: V1Account

    fun checkAccountStatus(block: (V1Acquirer, String) -> V1Account): V1Transaction {
        return apply {
            account = block(baseAcquirer, currency)
                .also {
                    // `targetAcquirer` 정보 잇는 경우, 검증 처리만 진행
                    targetAcquirer?.let { block(it, currency) }
                }
        }
    }

    fun checkAccountLimit(block: (V1Acquirer, String, BigDecimal) -> V1Account): V1Transaction {
        return apply {
            account = block(baseAcquirer, currency, if (status == PENDING) BigDecimal.ZERO else amount)
        }
    }

    fun checkCanBeExpired(): V1Transaction {
        if (status == COMPLETED) {
            throw InternalServiceException(ErrorCode.WITHDRAWAL_TRANSACTION_IS_COMPLETED)
        }
        return this
    }

    fun applyTransactionId(block: () -> Long): V1Transaction {
        return apply { id = block() }
    }

    fun changeStatusToPending(): V1Transaction {
        return apply { status = PENDING }
    }

    fun changeStatusToCompleted(): V1Transaction {
        return apply {
            status = COMPLETED
            completeDate = LocalDateTime.now()
        }
    }

    fun changeStatusToCanceled(): V1Transaction {
        return apply {
            status = CANCELED
            cancelDate = LocalDateTime.now()
        }
    }

    fun changeStatusToExpired(): V1Transaction {
        return apply { status = EXPIRED }
    }

    fun isNeedsExpireTransaction(block: (V1Transaction) -> Unit): V1Transaction {
        if (baseAcquirer.type == MTO_FUNDING && type == WITHDRAWAL && purpose == REMITTANCE && status == PENDING) {
            // MTO 펀딩 계좌 '송금 출금 대기' 거래인 경우
            block(this)
        }
        return this
    }

    fun isNeedsReverseTransaction(block: (V1Transaction) -> Unit): V1Transaction {
        if (baseAcquirer.type == FX_DEPOSIT && type == WITHDRAWAL && purpose == TransactionPurpose.FUNDING) {
            // 외화 예치금 계좌의 'MTO 펀딩 출금' 거래인 경우
            block(this)
        }
        return this
    }

    fun toCanceled(trReferenceId: String): V1Transaction {
        return this.copy(
            id = null,
            trReferenceId = trReferenceId,
            channel = this.channel,
            type = this.type.reverseType(),
            purpose = this.purpose.cancelPurpose(),
            transferDate = LocalDateTime.now().convertPatternOf(),
            cancelDate = null,
            orgTransactionId = this.id,
            orgTrReferenceId = this.trReferenceId,
            requestBy = this.channel.name,
            requestNote = this.channel.note,
            status = CREATED
        )
    }

}