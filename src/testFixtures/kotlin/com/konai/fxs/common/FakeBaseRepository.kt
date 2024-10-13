package com.konai.fxs.common

import com.konai.fxs.common.entity.BaseEntity
import java.security.SecureRandom

open class FakeBaseRepository<T : BaseEntity> {

    private val entities = mutableMapOf<Long, T>()

    protected fun save(entity: T): T {
        val id = entity.id ?: SecureRandom().nextLong()
        entities[id] = entity
        return entity.apply { this.id = id }
    }

    protected fun findById(id: Long): T? {
        return entities[id]
    }

}