package com.konai.fxs.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

const val COMPONENT_NAME = "FXS"
const val COMPONENT_CODE = "210"

const val EMPTY = ""
const val ZERO = 0
const val ZERO_STR = "0"
const val NONE = "NONE"
const val DEFAULT_SORT_ORDER = "DESC"
const val DEFAULT_SORT_BY = "id"
const val DEFAULT_REQUEST_BY = "FXS_SYSTEM"
const val DEFAULT_SEQUENCE_DATE = "YYYYMMDD"

const val HEADER_REQUEST_START_TIME = "X-KM-Request-Start-Time"

object Currency {
    const val KRW = "KRW"
    const val USD = "USD"
    const val EUR = "EUR"
}

val Dispatchers.VIRTUAL_THREAD: ExecutorCoroutineDispatcher
    get() = Executors.newSingleThreadExecutor().asCoroutineDispatcher()