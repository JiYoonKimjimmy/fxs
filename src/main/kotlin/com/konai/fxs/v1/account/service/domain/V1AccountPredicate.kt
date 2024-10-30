package com.konai.fxs.v1.account.service.domain

import com.konai.fxs.common.enumerate.AccountStatus
import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import java.math.BigDecimal

data class V1AccountPredicate(
    val id: Long? = null,
    val acquirer: V1AcquirerPredicate? = null,
    val currency: String? = null,
    val balance: BigDecimal? = null,
    val minRequiredBalance: BigDecimal? = null,
    val averageExchangeRate: BigDecimal? = null,
    val status: AccountStatus? = null
) {

    data class V1AcquirerPredicate(
        val id: String? = null,
        val type: AcquirerType? = null,
        val name: String? = null
    ) {
        constructor(acquirer: V1Acquirer): this(
            id = acquirer.id,
            type = acquirer.type,
            name = acquirer.name
        )

        fun toDomain(): V1Acquirer? {
            return if (id != null && type != null && name != null) {
                V1Acquirer(id = id, type = type, name = name)
            } else {
                null
            }
        }
    }

}