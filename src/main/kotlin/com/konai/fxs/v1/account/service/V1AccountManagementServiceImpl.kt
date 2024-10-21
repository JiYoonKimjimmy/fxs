package com.konai.fxs.v1.account.service

import com.konai.fxs.v1.account.repository.V1AccountRepository
import com.konai.fxs.v1.account.service.domain.V1Account
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
class V1AccountManagementServiceImpl(
    private val v1AccountRepository: V1AccountRepository
) : V1AccountManagementService {

    override fun create(domain: V1Account): V1Account {
        return v1AccountRepository.save(domain)
    }

    override fun findOne(id: Long): V1Account {
        return v1AccountRepository.findOne(id) ?: throw EntityNotFoundException()
    }

}