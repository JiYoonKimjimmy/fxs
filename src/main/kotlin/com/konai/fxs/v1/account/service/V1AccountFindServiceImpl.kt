package com.konai.fxs.v1.account.service

import com.konai.fxs.v1.account.repository.V1AccountRepository
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class V1AccountFindServiceImpl(
    private val v1AccountRepository: V1AccountRepository
) : V1AccountFindService {

    override fun findByPredicate(predicate: V1AccountPredicate): V1Account? {
        return v1AccountRepository.findByPredicate(predicate)
    }

    override fun existsByAcquirer(acquirer: V1Acquirer): Boolean {
        return v1AccountRepository.existsByAcquirer(acquirer)
    }

}