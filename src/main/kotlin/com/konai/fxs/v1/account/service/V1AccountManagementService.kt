package com.konai.fxs.v1.account.service

import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate

interface V1AccountManagementService {

    fun save(domain: V1Account): V1Account

    fun findByPredicate(predicate: V1AccountPredicate): V1Account

    fun update(predicate: V1AccountPredicate): V1Account

}