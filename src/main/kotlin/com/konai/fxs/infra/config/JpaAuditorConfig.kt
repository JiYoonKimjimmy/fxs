package com.konai.fxs.infra.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@Configuration
class JpaAuditorConfig {

    @Bean
    fun auditorProvider(): AuditorAware<String> {
        return CustomAuditorAware()
    }

}