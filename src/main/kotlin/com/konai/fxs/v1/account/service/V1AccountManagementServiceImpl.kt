package com.konai.fxs.v1.account.service

import com.konai.fxs.common.model.BasePageable
import com.konai.fxs.common.model.PageableRequest
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.ResourceNotFoundException
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate
import org.springframework.stereotype.Service

@Service
class V1AccountManagementServiceImpl(
    private val v1AccountSaveService: V1AccountSaveService,
    private val v1AccountFindService: V1AccountFindService
) : V1AccountManagementService {

    override fun save(account: V1Account): V1Account {
        return account
            .checkDuplicatedAcquirer(v1AccountFindService::existsByAcquirer)
            .let { v1AccountSaveService.save(it) }
    }

    override fun findByPredicate(predicate: V1AccountPredicate): V1Account {
        return v1AccountFindService.findByPredicate(predicate)
            ?: throw ResourceNotFoundException(ErrorCode.ACCOUNT_NOT_FOUND)
    }

    override fun findAllByPredicate(predicate: V1AccountPredicate, pageable: PageableRequest): BasePageable<V1Account> {
        return v1AccountFindService.findAllByPredicate(predicate, pageable)
    }

    override fun update(predicate: V1AccountPredicate): V1Account {
        return findByPredicate(predicate = V1AccountPredicate(id = predicate.id))
            .checkCanBeUpdated()
            .update(predicate = predicate)
            .let { save(it) }
    }

}