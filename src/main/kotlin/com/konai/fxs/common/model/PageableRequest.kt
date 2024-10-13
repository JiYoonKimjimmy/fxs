package com.konai.fxs.common.model

import com.konai.fxs.common.DEFAULT_SORT_ORDER

data class PageableRequest(
    val number: Int = 0,
    val size: Int = 10,
    val fromDate: String? = null,
    val toDate: String? = null,
    var sortBy: String? = null,
    val sortOrder: String? = DEFAULT_SORT_ORDER
)