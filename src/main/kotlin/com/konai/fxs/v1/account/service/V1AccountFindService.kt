package com.konai.fxs.v1.account.service

import com.konai.fxs.common.model.BasePageable
import com.konai.fxs.common.model.PageableRequest
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate.V1AcquirerPredicate

interface V1AccountFindService {

    fun findByPredicate(predicate: V1AccountPredicate): V1Account?

    fun findAllByPredicate(predicate: V1AccountPredicate, pageable: PageableRequest): BasePageable<V1Account>

    fun findByAcquirer(acquirer: V1Acquirer, currency: String): V1Account?

    fun existsByAcquirer(acquirer: V1AcquirerPredicate, id: Long? = null): Boolean

}