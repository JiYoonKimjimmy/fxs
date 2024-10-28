package com.konai.fxs.v1.account.service

import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.ResourceNotFoundException
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class V1AccountManagementServiceImpl(
    private val v1AccountSaveService: V1AccountSaveService,
    private val v1AccountFindService: V1AccountFindService
) : V1AccountManagementService {

    override fun create(domain: V1Account): V1Account {
        return domain
            .checkDuplicatedAcquirer(v1AccountFindService::existsByAcquirer)
            .let { v1AccountSaveService.save(it) }
    }

    override fun findByPredicate(predicate: V1AccountPredicate): V1Account {
        return v1AccountFindService.findByPredicate(predicate)
            ?: throw ResourceNotFoundException(ErrorCode.ACCOUNT_NOT_FOUND)
    }

}