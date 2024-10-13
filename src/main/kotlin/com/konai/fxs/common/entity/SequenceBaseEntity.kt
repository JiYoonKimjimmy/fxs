package com.konai.fxs.common.entity

import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
abstract class SequenceBaseEntity(
    @Id open var id: Long? = null
) : BaseEntity()