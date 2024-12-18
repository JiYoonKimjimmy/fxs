package com.konai.fxs.infra.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ApplicationProperties(

    @Value("\${koreaexim-api.key}")
    val koreaeximApiKey: String,
    @Value("\${koreaexim-api.type}")
    val koreaeximApiType: String,

)