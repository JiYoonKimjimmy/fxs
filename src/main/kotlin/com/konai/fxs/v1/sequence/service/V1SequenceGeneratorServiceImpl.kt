package com.konai.fxs.v1.sequence.service

import com.konai.fxs.common.enumerate.SequenceType.TRANSACTION_SEQUENCE
import com.konai.fxs.common.lock.DistributedLockManager
import com.konai.fxs.v1.sequence.repository.V1SequenceGeneratorRepository
import org.springframework.stereotype.Service

@Service
class V1SequenceGeneratorServiceImpl(
    private val v1SequenceGeneratorRepository: V1SequenceGeneratorRepository,
    private val distributedLockManager: DistributedLockManager
) : V1SequenceGeneratorService {

    override fun nextTransactionSequence(): Long {
        return distributedLockManager.sequenceLock(TRANSACTION_SEQUENCE) {
            v1SequenceGeneratorRepository.next(TRANSACTION_SEQUENCE).value
        }
    }

}