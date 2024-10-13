package com.konai.fxs.common.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

const val DATE_BASIC_PATTERN = "yyyyMMdd"
const val TIME_BASIC_PATTERN = "HHmmss"
const val DATE_TIME_BASIC_PATTERN = "yyyyMMddHHmmss"

fun LocalDate.convertPatternOf(pattern: String = DATE_BASIC_PATTERN): String {
    return this.format(DateTimeFormatter.ofPattern(pattern))
}

fun LocalTime.convertPatternOf(pattern: String = TIME_BASIC_PATTERN): String {
    return this.format(DateTimeFormatter.ofPattern(pattern))
}

fun LocalDateTime.convertPatternOf(pattern: String = DATE_TIME_BASIC_PATTERN): String {
    return this.format(DateTimeFormatter.ofPattern(pattern))
}

fun String.convertPatternOf(pattern: String = DATE_TIME_BASIC_PATTERN): LocalDateTime {
    return LocalDateTime.parse(this, DateTimeFormatter.ofPattern(pattern))
}