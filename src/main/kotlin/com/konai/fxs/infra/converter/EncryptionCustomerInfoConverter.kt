package com.konai.fxs.infra.converter

import com.konai.fxs.common.util.DBEncryptUtil
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class EncryptionCustomerInfoConverter : AttributeConverter<String, String> {

    override fun convertToDatabaseColumn(attribute: String?) = attribute?.let { DBEncryptUtil.encryptCustomInfo(it) }

    override fun convertToEntityAttribute(dbData: String?) = dbData?.let { DBEncryptUtil.decryptCustomInfo(it) }

}