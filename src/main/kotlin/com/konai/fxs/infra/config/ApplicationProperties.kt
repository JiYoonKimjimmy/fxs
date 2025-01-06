package com.konai.fxs.infra.config

import com.konai.fxs.common.EMPTY
import com.konai.fxs.common.ZERO
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ApplicationProperties(

    @Value("\${koreaexim-api.key}")
    val koreaeximApiKey: String = EMPTY,
    @Value("\${koreaexim-api.type}")
    val koreaeximApiType: String = EMPTY,
    @Value("\${koreaexim-api.collector.size}")
    val koreaeximCollectorSize: Int = ZERO,

)