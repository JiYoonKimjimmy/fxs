package com.konai.fxs.common.lock

import com.konai.fxs.common.enumerate.DistributedLockType
import com.konai.fxs.v1.account.service.domain.V1Account
import java.util.concurrent.TimeUnit

interface DistributedLockManager {

    fun <R> lock(
        key: String,
        waitTime: Long = 20,
        leaseTime: Long = 60,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        block: () -> R
    ): R

    fun <R> accountLock(
        lockType: DistributedLockType,
        account: V1Account,
        block: () -> R
    ): R

}