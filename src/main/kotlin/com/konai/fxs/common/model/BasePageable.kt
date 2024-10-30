package com.konai.fxs.common.model

open class BasePageable<T>(
    open val pageable: Pageable = Pageable(),
    open val content: List<T> = emptyList(),
) {

    data class Pageable(
        val first: Boolean = true,
        val last: Boolean = false,
        val number: Int = 0,
        val numberOfElements: Int = 0,
        val size: Int = 10,
        val totalPages: Int = 0,
        val totalElements: Long = 0,
    )

}