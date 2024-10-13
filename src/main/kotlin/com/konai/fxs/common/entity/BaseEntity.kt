package com.konai.fxs.common.entity

import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
abstract class BaseEntity(
    @Id
    open var id: Long? = null
)