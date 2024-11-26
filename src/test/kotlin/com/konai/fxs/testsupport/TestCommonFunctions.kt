package com.konai.fxs.testsupport

import com.konai.fxs.common.enumerate.AccountStatus
import com.konai.fxs.testsupport.TestDependencies.fakeV1AccountRepository
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate
import java.math.BigDecimal

object TestCommonFunctions {

    fun saveAccount(
        account: V1Account,
        balance: BigDecimal? = null,
        averageExchangeRate: BigDecimal? = null,
        status: AccountStatus? = null
    ): V1Account {
        val predicate = V1AccountPredicate(
            balance = balance,
            averageExchangeRate = averageExchangeRate,
            status = status
        )
        return fakeV1AccountRepository.save(account.update(predicate))
    }

}