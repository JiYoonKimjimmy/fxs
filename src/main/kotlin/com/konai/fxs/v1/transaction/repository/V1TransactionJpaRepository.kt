package com.konai.fxs.v1.transaction.repository

import com.konai.fxs.v1.transaction.repository.entity.V1TransactionEntity
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository

interface V1TransactionJpaRepository : JpaRepository<V1TransactionEntity, Long>, KotlinJdslJpqlExecutor