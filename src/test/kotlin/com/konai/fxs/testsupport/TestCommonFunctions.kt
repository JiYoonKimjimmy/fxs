package com.konai.fxs.testsupport

import com.konai.fxs.testsupport.TestDependencies.v1AccountSaveService
import com.konai.fxs.v1.account.service.domain.V1Account

object TestCommonFunctions {

    fun saveAccount(vararg accounts: V1Account) {
        accounts.forEach(v1AccountSaveService::save)
    }

}