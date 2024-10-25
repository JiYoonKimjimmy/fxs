package com.konai.fxs.v1.account.service

import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate
import com.konai.fxs.v1.account.service.domain.V1Acquirer

interface V1AccountFindService {

    fun findByPredicate(predicate: V1AccountPredicate): V1Account?

    fun existsByAcquirer(acquirer: V1Acquirer): Boolean

}