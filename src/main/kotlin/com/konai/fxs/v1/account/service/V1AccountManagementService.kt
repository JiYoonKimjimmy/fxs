package com.konai.fxs.v1.account.service

import com.konai.fxs.v1.account.service.domain.V1Account

interface V1AccountManagementService {

    fun create(domain: V1Account): V1Account

    fun findOne(id: Long): V1Account

}