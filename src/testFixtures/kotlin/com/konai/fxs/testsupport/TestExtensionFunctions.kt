package com.konai.fxs.testsupport

import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.v1.account.repository.entity.V1AccountEntity.V1AcquirerEntity
import java.util.*
import java.util.concurrent.atomic.AtomicLong

object TestExtensionFunctions {

    fun generateUUID(length: Int = 10): String {
        return UUID.randomUUID().toString().replace("-", "").substring(0, length)
    }

    private val sequence = AtomicLong()
    fun generateSequence(id: Long? = null): Long {
        return id ?: sequence.incrementAndGet()
    }

    fun generateAcquirerEntity(): V1AcquirerEntity = V1AcquirerEntity(
        id = generateUUID(),
        type = AcquirerType.FX_DEPOSIT,
        name = "외화 예치금 계좌"
    )

}