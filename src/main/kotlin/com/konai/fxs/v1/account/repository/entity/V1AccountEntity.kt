package com.konai.fxs.v1.account.repository.entity

import com.konai.fxs.common.entity.SequenceBaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Table

@Table(name = "ACCOUNTS")
@Entity
class V1AccountEntity(

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Long? = null,
    val accountNumber: String

) : SequenceBaseEntity()