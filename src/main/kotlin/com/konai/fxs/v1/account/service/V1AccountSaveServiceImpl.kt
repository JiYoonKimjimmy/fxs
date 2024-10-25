package com.konai.fxs.v1.account.service

import com.konai.fxs.v1.account.repository.V1AccountRepository
import com.konai.fxs.v1.account.service.domain.V1Account
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class V1AccountSaveServiceImpl(
    private val v1AccountRepository: V1AccountRepository
) : V1AccountSaveService {

    @Transactional
    override fun save(domain: V1Account): V1Account {
        return v1AccountRepository.save(domain)
    }

}