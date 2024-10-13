package com.konai.fxs.infra.error.exception

import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.common.error
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.slf4j.LoggerFactory

open class BaseException(val errorCode: ErrorCode, var detailMessage: String? = null): RuntimeException() {
    // logger
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun parseDetailMessage(): Pair<String, String>? {
        return try {
            detailMessage
                ?.substringAfter(": ")?.trim('"')
                ?.let { Json.parseToJsonElement(it).jsonObject }
                ?.let { Pair(it.getJsonContent("reason"), it.getJsonContent("message")) }
        } catch (e: NullPointerException) {
            logger.error(e)
            null
        }
    }

    private fun JsonObject.getJsonContent(key: String): String {
        return this[key]?.jsonPrimitive?.content!!
    }

}