package com.konai.fxs.infra.converter

import com.konai.fxs.common.util.DATE_TIME_BASIC_PATTERN
import com.konai.fxs.common.util.convertPatternOf
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.time.LocalDateTime

@Converter
class LocalDateTimeConverter : AttributeConverter<String, LocalDateTime> {

    override fun convertToDatabaseColumn(source: String?): LocalDateTime? {
        return source?.convertPatternOf(DATE_TIME_BASIC_PATTERN)
    }

    override fun convertToEntityAttribute(source: LocalDateTime?): String? {
        return source?.convertPatternOf(DATE_TIME_BASIC_PATTERN)
    }

}