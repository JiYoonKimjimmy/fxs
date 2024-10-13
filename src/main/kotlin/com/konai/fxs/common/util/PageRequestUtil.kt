package com.konai.fxs.common.util

import com.konai.fxs.common.model.BasePageable
import com.konai.fxs.common.model.BasePageable.Pageable
import com.konai.fxs.common.model.PageableRequest
import com.konai.fxs.common.DEFAULT_SORT_BY
import com.konai.fxs.common.DEFAULT_SORT_ORDER
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.domain.Sort

object PageRequestUtil {

    fun PageableRequest.toPageRequest(): PageRequest {
        return PageRequest.of(
            this.number,
            this.size,
            Sort.Direction.valueOf(this.sortOrder ?: DEFAULT_SORT_ORDER),
            *this.sortBy?.split(",")?.toTypedArray() ?: arrayOf(DEFAULT_SORT_BY)
        )
    }

    fun <T> Page<T>.toBasePageable(): BasePageable<T> {
        return BasePageable(
            pageable = Pageable(
                first = this.isFirst,
                last = this.isLast,
                number = this.number,
                numberOfElements = this.numberOfElements,
                size = this.size,
                totalPages = this.totalPages,
                totalElements = this.numberOfElements,
            ),
            content = this.content
        )
    }

    fun <T> Slice<T>.toBasePageable(): BasePageable<T> {
        return BasePageable(
            pageable = Pageable(
                first = this.isFirst,
                last = this.isLast,
                number = this.number,
                numberOfElements = this.numberOfElements,
                size = this.size,
                totalElements = this.numberOfElements,
            ),
            content = this.content
        )
    }

}