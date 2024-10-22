package com.konai.fxs.v1.account.service

import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import com.konai.fxs.infra.error.exception.ResourceNotFoundException
import com.konai.fxs.v1.account.repository.V1AccountRepository
import com.konai.fxs.v1.account.service.domain.V1Account
import org.springframework.stereotype.Service

@Service
class V1AccountManagementServiceImpl(
    private val v1AccountRepository: V1AccountRepository
) : V1AccountManagementService {

    override fun create(domain: V1Account): V1Account {
        return v1AccountRepository.existsByAcquirer(domain.acquirer)
            .takeIf { it.not() }
            ?.let { v1AccountRepository.save(domain) }
            ?: throw InternalServiceException(ErrorCode.ACCOUNT_ACQUIRER_IS_DUPLICATED)
    }

    override fun findOne(id: Long): V1Account {
        return v1AccountRepository.findOne(id) ?: throw ResourceNotFoundException(ErrorCode.ACCOUNT_NOT_FOUND)
    }

}