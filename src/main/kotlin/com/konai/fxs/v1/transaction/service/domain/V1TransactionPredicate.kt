package com.konai.fxs.v1.transaction.service.domain

import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.enumerate.TransactionPurpose
import com.konai.fxs.common.enumerate.TransactionStatus
import com.konai.fxs.common.enumerate.TransactionType
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate.V1AcquirerPredicate
import java.math.BigDecimal

data class V1TransactionPredicate(
    val id: Long? = null,
    val trReferenceId: String? = null,
    val baseAcquirer: V1AcquirerPredicate? = null,
    val targetAcquirer: V1AcquirerPredicate? = null,
    val type: TransactionType? = null,
    val purpose: TransactionPurpose? = null,
    val channel: TransactionChannel? = null,
    val currency: String? = null,
    val amount: BigDecimal? = null,
    val exchangeRate: BigDecimal? = null,
    val transferDate: String? = null,
    val requestBy: String? = null,
    val status: TransactionStatus? = null,
    val orgTrReferenceId: String? = null,
)