package com.konai.fxs.infra.config

import com.konai.fxs.common.COMPONENT_NAME
import org.springframework.data.domain.AuditorAware
import java.util.*

class CustomAuditorAware : AuditorAware<String> {
    override fun getCurrentAuditor(): Optional<String> {
        return Optional.of(COMPONENT_NAME)
    }
}