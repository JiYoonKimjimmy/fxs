package com.konai.fxs.v1.account.repository

import com.konai.fxs.v1.account.repository.entity.V1AccountEntity
import org.springframework.data.jpa.repository.JpaRepository

interface V1AccountJpaRepository : JpaRepository<V1AccountEntity, Long>