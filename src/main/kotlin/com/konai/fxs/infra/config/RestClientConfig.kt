package com.konai.fxs.infra.config

import com.konai.fxs.common.HEADER_REQUEST_START_TIME
import com.konai.fxs.common.ZERO_STR
import com.konai.fxs.common.enumerate.ExternalComponent
import com.konasl.commonlib.springweb.correlation.core.RequestContext
import com.konasl.commonlib.springweb.correlation.headerpropagator.CorrelationHeaderField.*
import com.konasl.commonlib.springweb.correlation.loggercontext.CorrelationLoggingField.*
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import java.net.http.HttpClient
import java.time.Duration
import java.time.Instant
import java.util.concurrent.Executors
import javax.net.ssl.SSLContext

@Configuration
class RestClientConfig(
    private val externalUrlProperties: ExternalUrlProperties
) : RestClientAutoConfiguration() {
    // logger
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun <T> restClientHttpServiceProxy(component: ExternalComponent, classType: Class<T>): T {
        val url = externalUrlProperties.getProperty(component).url
        val adapter = RestClientAdapter.create(restClient(component, url, Duration.ofSeconds(5), Duration.ofSeconds(20)))
        val factory = HttpServiceProxyFactory.builderFor(adapter).build()
        return factory.createClient(classType)
    }

    private fun restClient(component: ExternalComponent, baseUrl: String, connectTimeout: Duration, readTimeout: Duration): RestClient {
        return RestClient
            .builder()
            .baseUrl(baseUrl)
            .requestFactory(jdkClientHttpRequestFactory(connectTimeout, readTimeout))
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .requestInitializer { request -> requestHeaders(request) }
            .requestInterceptor { request, body, execution ->
                request.logging(body, component)
                execution.execute(request, body).logging(request, component)
            }
            .build()
    }

    private fun jdkClientHttpRequestFactory(connectTimeout: Duration, readTimeout: Duration): JdkClientHttpRequestFactory {
        return JdkClientHttpRequestFactory(customHttpClient(connectTimeout)).apply {
            this.setReadTimeout(readTimeout)
        }
    }

    private fun customHttpClient(connectTimeout: Duration): HttpClient {
        return HttpClient
            .newBuilder()
            .connectTimeout(connectTimeout)
            .executor(Executors.newVirtualThreadPerTaskExecutor())
            .sslContext(SSLContext.getDefault())
            .build()
    }

    private fun requestHeaders(request: HttpRequest) {
        request.headers[HEADER_REQUEST_START_TIME]             = Instant.now().toString()
        request.headers[ASP_ID_HEADER_FIELD.getName()]         = MDC.get(ASP_ID_LOG_FIELD.getName()) ?: ASP_ID_LOG_FIELD.getName()
        request.headers[MPA_ID_HEADER_FIELD.getName()]         = MDC.get(MPA_ID_LOG_FIELD.getName()) ?: MPA_ID_LOG_FIELD.getName()
        request.headers[USER_ID_HEADER_FIELD.getName()]        = MDC.get(USER_ID_LOG_FIELD.getName()) ?: USER_ID_LOG_FIELD.getName()
        request.headers[CORRELATION_ID_HEADER_FIELD.getName()] = MDC.get(CORRELATION_ID_LOG_FIELD.getName()) ?: RequestContext.generateId()
    }

    private fun HttpRequest.logging(body: ByteArray, component: ExternalComponent): String {
        logger.info("[${component.name}-REQ] ${this.method} ${this.uri} ${String(body)}")
        return Instant.now().toString()
    }

    private fun ClientHttpResponse.logging(request: HttpRequest, component: ExternalComponent): ClientHttpResponse {
        val responseTime = Duration.between(Instant.parse(request.headers[HEADER_REQUEST_START_TIME]?.first() ?: ZERO_STR), Instant.now()).toMillis()
        logger.info("[${component.name}-RES] ${request.method} ${request.uri} ${this.statusCode.value()} ${responseTime}ms")
        return this
    }

}