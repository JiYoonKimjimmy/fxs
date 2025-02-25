package com.konai.fxs.common

import com.konai.fxs.infra.error.exception.BaseException
import com.konasl.commonlib.springweb.correlation.core.ContextField
import com.konasl.commonlib.springweb.correlation.core.RequestContext
import com.konasl.commonlib.springweb.correlation.loggercontext.CorrelationLoggingField
import org.slf4j.Logger
import org.slf4j.MDC
import org.springframework.data.domain.Slice

fun getCorrelationId(): String {
    return RequestContext.get(ContextField.CORRELATION_ID) ?: MDC.get(CorrelationLoggingField.CORRELATION_ID_LOG_FIELD.getName()) ?: RequestContext.generateId()
}

fun setCorrelationId(correlationId: String?) {
    MDC.put(CorrelationLoggingField.CORRELATION_ID_LOG_FIELD.getName(), correlationId)
}

fun <T> Slice<T?>.firstOrNull(): T? {
    return this.content.ifEmpty { listOf(null) }.first()
}

fun <T> T?.ifNotNullEquals(target: T?): Boolean {
    return when (this) {
        is String -> this.emptyStringToNull()?.let { target == it } ?: true
        else -> this?.let { target == it } ?: true
    }
}

fun <T> T?.ifNull(value: T): T {
    return this ?: value
}

fun Logger.error(exception: Exception): Exception {
    when (exception) {
        is BaseException -> this.error("${exception.errorCode.message}. ${exception.detailMessage}", exception)
        else -> this.error(exception.message, exception)
    }
    return exception
}

fun String.emptyStringToNull(): String? {
    return this.ifEmpty { null }
}

fun String.convertToDouble(): Double {
    return this.replace(",", EMPTY).toDouble()
}