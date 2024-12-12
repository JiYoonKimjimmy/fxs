package com.konai.fxs.testsupport

import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.v1.account.controller.model.V1AcquirerModel
import com.konai.fxs.v1.account.repository.entity.V1AccountEntity.V1AcquirerEntity
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate.V1AcquirerPredicate
import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.FixtureMonkeyBuilder
import com.navercorp.fixturemonkey.jackson.plugin.JacksonPlugin
import com.navercorp.fixturemonkey.jakarta.validation.plugin.JakartaValidationPlugin
import com.navercorp.fixturemonkey.kotest.KotestPlugin
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import java.util.*
import java.util.concurrent.atomic.AtomicLong

object TestExtensionFunctions {

    val fixtureMonkey: FixtureMonkey = FixtureMonkeyBuilder()
        .plugin(KotlinPlugin())
        .plugin(KotestPlugin())
        .plugin(JacksonPlugin())
        .plugin(JakartaValidationPlugin())
        .build()

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

    fun V1Acquirer.toModel(): V1AcquirerModel {
        return V1AcquirerModel(acquirerId = this.id, acquirerType = this.type, acquirerName = this.name)
    }

    fun V1Acquirer.toPredicate(): V1AcquirerPredicate {
        return V1AcquirerPredicate(id = this.id, type = this.type, name = this.name)
    }

}