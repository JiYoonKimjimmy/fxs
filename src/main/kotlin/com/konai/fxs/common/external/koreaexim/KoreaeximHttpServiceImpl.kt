package com.konai.fxs.common.external.koreaexim

import com.konai.fxs.common.error
import com.konai.fxs.common.getCorrelationId
import com.konai.fxs.infra.config.ApplicationProperties
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import org.slf4j.LoggerFactory
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service

@Service
class KoreaeximHttpServiceImpl(
    private val koreaeximHttpServiceProxy: KoreaeximHttpServiceProxy,
    private val applicationProperties: ApplicationProperties,
) : KoreaeximHttpService {
    // logger
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Retryable(
        retryFor = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 500, multiplier = 2.0)
    )
    override fun getExchangeRates(searchDate: String): KoreaeximExchangeRateResult {
        return koreaeximHttpServiceProxy.getExchangeRate(
                apiKey = applicationProperties.koreaeximApiKey,
                apiType = applicationProperties.koreaeximApiType,
                searchDate = searchDate
            )
            .let { checkSuccessResponse(it) }
            .let { KoreaeximExchangeRateResult(it, searchDate) }
    }

    @Recover
    fun recoverGetExchangeRates(e: Exception, searchDate: String): KoreaeximExchangeRateResult {
        logger.error("[${getCorrelationId()}] FAILED KOREAEXIM API.", e)
        return KoreaeximExchangeRateResult(searchDate = searchDate)
    }

    private fun checkSuccessResponse(response: List<KoreaeximExchangeRateResponse>): List<KoreaeximExchangeRateResponse> {
        if (response.isEmpty()) {
            handleErrorResponse()
        } else {
            // API 연동 결과 `result != 1` 인 경우, 에러 처리
            response.first().result.takeIf { it != 1 }?.let { handleErrorResponse(it) }
        }
        return response
    }

    private fun handleErrorResponse(result: Int? = null) {
        val errorCode = when (result) {
            // DATA 오류 : 요청 `data` 정보가 잘못된 경우
            2 -> ErrorCode.KOREAEXIM_API_TYPE_IS_INVALID
            // 인증코드 오류 : 요청 `authKey` 만료 or 잘못된 경우
            3 -> ErrorCode.KOREAEXIM_API_KEY_IS_INVALID
            // 일일횟수 초과 : 일일 요청 횟수 초과인 경우
            4 -> ErrorCode.KOREAEXIM_API_REQUEST_LIMIT_EXCEEDED
            // 그 외, 미정의 에러 처리
            else -> ErrorCode.KOREAEXIM_API_UNKNOWN_ERROR
        }
        logger.error(InternalServiceException(errorCode, "[result : $result]"))
    }

}