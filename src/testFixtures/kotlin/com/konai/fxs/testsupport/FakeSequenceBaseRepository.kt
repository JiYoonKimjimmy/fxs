package com.konai.fxs.testsupport

import com.konai.fxs.common.entity.SequenceBaseEntity
import com.konai.fxs.common.model.PageableRequest

open class FakeSequenceBaseRepository<T : SequenceBaseEntity> {

    protected val entities = mutableMapOf<Long, T>()

    protected fun save(entity: T): T {
        val id = entity.id ?: TestExtensionFunctions.generateSequence()
        entities[id] = entity
        return entity.apply { this.id = id }
    }

    protected fun findById(id: Long): T? {
        return entities[id]
    }

    protected fun findPage(pageable: PageableRequest, predicate: (T) -> Boolean): Pair<Int, List<T>> {
        val total = this.entities.values.filter { predicate(it) }
        val start = pageable.number * pageable.size
        val last = ((pageable.number + 1) * pageable.size) - 1
        val end = if (total.size < last) total.size else last
        val content = total.subList(start, end)
        return Pair(total.size, content)
    }

}