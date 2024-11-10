package com.konai.fxs.common

import org.slf4j.Logger
import org.springframework.data.domain.Slice

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
    this.error(exception.message, exception)
    return exception
}

fun String.emptyStringToNull(): String? {
    return this.ifEmpty { null }
}