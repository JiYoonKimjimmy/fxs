package com.konai.fxs.v1.exchangerate.koreaexim.service

import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.redis.EmbeddedRedisTestListener
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRatePredicate
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class V1KoreaeximExchangeRateCollectServiceImplTest : CustomBehaviorSpec({

    listeners(EmbeddedRedisTestListener())

    val v1KoreaeximExchangeRateCollectService = dependencies.v1KoreaeximExchangeRateCollectService
    val fakeV1KoreaeximExchangeRateRepository = dependencies.fakeV1KoreaeximExchangeRateRepository
    val v1KoreaeximExchangeRateCacheRepository = dependencies.v1KoreaeximExchangeRateCacheRepository
    
    given("한국수출입은행 환율 정보 조회 요청되어") {
        val index = 1
        val searchDate = "20241217"

        `when`("Koreaexim API 응답 결과 'RESULT: 2' 에러인 경우") {
            
            then("'API 요청 데이터 오류 에러 코드' 예외 발생 정상 확인한다") {
                
            }
        }
        
        `when`("Koreaexim API 응답 결과 'RESULT: 3' 에러인 경우") {

            then("'API 인증키 만료 에러 코드' 예외 발생 정상 확인한다") {
                
            }
        }

        `when`("Koreaexim API 응답 결과 'RESULT: 4' 에러인 경우") {

            then("'API 호출 횟수 초과 에러 코드' 예외 발생 정상 확인한다") {
                
            }
        }

        `when`("API 연동 정상 조회 성공인 경우") {
            val result = v1KoreaeximExchangeRateCollectService.collect(index, searchDate)

            then("조회 결과 정상 확인한다") {
                result.shouldNotBeEmpty()
            }

            then("Koreaexim 환율 정보 내역 저장 정상 확인한다") {
                val exchangeRate = result.first()
                val predicate = V1KoreaeximExchangeRatePredicate(registerDate = searchDate, curUnit = exchangeRate.curUnit)
                val saved = fakeV1KoreaeximExchangeRateRepository.findByPredicate(predicate)
                saved!! shouldNotBe null
                saved.curNm shouldBe exchangeRate.curNm
            }

            then("최근 환율 정보 Cache 변경 정상 확인한다") {
                val exchangeRate = result.first()
                val saved = v1KoreaeximExchangeRateCacheRepository.findKoreaeximExchangeRateCache(exchangeRate.curUnit)
                saved!! shouldNotBe null
                saved.curNm shouldBe exchangeRate.curNm
            }
        }
    }
    
})