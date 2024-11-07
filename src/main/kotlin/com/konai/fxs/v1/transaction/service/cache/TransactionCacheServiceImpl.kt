package com.konai.fxs.v1.transaction.service.cache

import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.transaction.repository.cache.TransactionCacheRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class TransactionCacheServiceImpl(
    private val transactionCacheRepository: TransactionCacheRepository
) : TransactionCacheService {

    override fun findWithdrawalReadyTotalAmountCache(acquirer: V1Account.V1Acquirer): BigDecimal {
        return transactionCacheRepository.findWithdrawalReadyTotalAmountCache(acquirer.id, acquirer.type)
            ?.let { BigDecimal.valueOf(it.toLong()) }
            ?: BigDecimal.ZERO
    }

}