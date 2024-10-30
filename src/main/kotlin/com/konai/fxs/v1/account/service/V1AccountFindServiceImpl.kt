package com.konai.fxs.v1.account.service

import com.konai.fxs.common.model.BasePageable
import com.konai.fxs.common.model.PageableRequest
import com.konai.fxs.v1.account.repository.V1AccountRepository
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate.V1AcquirerPredicate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class V1AccountFindServiceImpl(
    private val v1AccountRepository: V1AccountRepository
) : V1AccountFindService {

    override fun findByPredicate(predicate: V1AccountPredicate): V1Account? {
        // 요청 `predicate` 별 외화 계좌 정보 조회
        return v1AccountRepository.findByPredicate(predicate)
    }

    override fun findAllByPredicate(predicate: V1AccountPredicate, pageable: PageableRequest): BasePageable<V1Account> {
        return v1AccountRepository.findAllByPredicate(predicate, pageable)
    }

    override fun existsByAcquirer(acquirer: V1AcquirerPredicate, id: Long?): Boolean {
        // 요청 `acquirer` 조건 외화 계좌 정보 조회
        return findByPredicate(predicate = V1AccountPredicate(acquirer = acquirer))
            // 동일한 `acquirer` 다른 외화 계좌가 존재하는 경우, `ture` 반환 처리
            ?.let { it.id != id }
            ?: false
    }

}