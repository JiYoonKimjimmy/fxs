package com.konai.fxs.v1.account.repository

import com.konai.fxs.v1.account.service.domain.V1Account

interface V1AccountRepository {

    fun save(domain: V1Account): V1Account

    fun findOne(id: Long): V1Account?

}