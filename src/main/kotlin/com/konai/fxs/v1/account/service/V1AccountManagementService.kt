package com.konai.fxs.v1.account.service

import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate

interface V1AccountManagementService {

    fun create(domain: V1Account): V1Account

    fun findByPredicate(predicate: V1AccountPredicate): V1Account

}