package com.konai.fxs.v1.account.repository

import com.konai.fxs.v1.account.repository.entity.V1AccountEntity
import com.konai.fxs.v1.account.repository.entity.V1AcquirerEntity
import org.springframework.data.jpa.repository.JpaRepository

interface V1AccountJpaRepository : JpaRepository<V1AccountEntity, Long> {

    fun existsByAcquirer(acquirerEntity: V1AcquirerEntity): Boolean

}