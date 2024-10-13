package com.konai.fxs.v1.account.repository.entity

import com.konai.fxs.common.entity.BaseEntity
import jakarta.persistence.*

@Table(name = "ACCOUNTS")
@Entity
class V1AccountEntity(

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Long? = null,
    val accountNumber: String

) : BaseEntity(id)