package com.konai.fxs.common

import org.slf4j.Logger
import org.springframework.data.domain.Slice

fun <T> Slice<T>.getContentFirstOrNull(): T? {
    return this.content.ifEmpty { listOf(null) }.first()
}

fun <T> ifNotNullEquals(source: T?, target: T?): Boolean {
    return source?.let { target == it } ?: true
}

fun Logger.error(exception: Exception): Exception {
    this.error(exception.message, exception)
    return exception
}