package com.konai.fxs.infra.config

import com.konai.fxs.common.util.DBEncryptUtil
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class DataSourceConfig(
    @Value("\${spring.datasource.hikari.password}")
    private val password: String
) {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    fun getDataSource(dataSourceProperties: DataSourceProperties): HikariDataSource =
        dataSourceProperties
            .initializeDataSourceBuilder()
            .type(HikariDataSource::class.java)
            .password(DBEncryptUtil.decryptCustomInfo(password))
            .build()

}