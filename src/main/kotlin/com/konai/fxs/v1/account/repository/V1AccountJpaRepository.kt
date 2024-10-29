package com.konai.fxs.v1.account.repository

import com.konai.fxs.v1.account.repository.entity.V1AccountEntity
import com.konai.fxs.v1.account.repository.entity.V1AccountEntity.V1AcquirerEntity
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository

interface V1AccountJpaRepository : JpaRepository<V1AccountEntity, Long>, KotlinJdslJpqlExecutor {

    fun existsByAcquirer(acquirerEntity: V1AcquirerEntity): Boolean

}