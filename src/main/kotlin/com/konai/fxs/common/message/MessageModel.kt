package com.konai.fxs.common.message

import com.konai.fxs.v1.transaction.service.domain.V1Transaction
import com.konasl.commonlib.springweb.correlation.core.RequestContext
import com.konasl.commonlib.springweb.correlation.loggercontext.CorrelationLoggingField
import org.slf4j.MDC

open class BaseMessage(
    val correlationId: String = MDC.get(CorrelationLoggingField.CORRELATION_ID_LOG_FIELD.getName()) ?: RequestContext.generateId()
)

data class V1SaveTransactionMessage(
    val transaction: V1Transaction
) : BaseMessage()

data class V1ExpireTransactionMessage(
    val transactionId: Long,
    val amount: Long
) : BaseMessage()

data class V1CollectExchangeRateTimerMessage(
    val index: Int,
    val date: String
) : BaseMessage()