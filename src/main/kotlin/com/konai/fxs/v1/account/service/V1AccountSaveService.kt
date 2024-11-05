package com.konai.fxs.v1.account.service

import com.konai.fxs.v1.account.service.domain.V1Account

interface V1AccountSaveService {

    fun save(account: V1Account): V1Account

}