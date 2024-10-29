package com.konai.fxs.v1.account.repository

import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer

interface V1AccountRepository {

    fun save(domain: V1Account): V1Account

    fun findByPredicate(predicate: V1AccountPredicate): V1Account?

}