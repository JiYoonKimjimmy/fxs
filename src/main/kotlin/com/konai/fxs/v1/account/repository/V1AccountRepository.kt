package com.konai.fxs.v1.account.repository

import com.konai.fxs.common.model.BasePageable
import com.konai.fxs.common.model.PageableRequest
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate

interface V1AccountRepository {

    fun save(account: V1Account): V1Account

    fun saveAndFlush(account: V1Account): V1Account

    fun findByPredicate(predicate: V1AccountPredicate): V1Account?

    fun findAllByPredicate(predicate: V1AccountPredicate, pageable: PageableRequest): BasePageable<V1Account>

}