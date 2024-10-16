# Hello, FXS 😎

## Foreign EXchange Service 외환 관리 서비스

- 외화 계좌, 외화 계좌 잔액, 환율 평단가 관리
- 외화 계좌 거래 승인 트랜잭션 관리
- 외부 환율 조회 API 연동하여 실시간 환율 정보 조회/저장

## 프로젝트 역할

- 외화 계좌 정보 관리 
- 외화 계좌 거래 관리
- 환율 정보 관리
- 외화 계좌 거래 내역 대사(정산) 관리

---

## 프로젝트 구성

- Java 21
- Kotlin 2.0.10
- Gradle 8.6
- Spring Boot 3.3.2
- Spring Data JPA
- KotlinJDL

---

## 프로젝트 기본 정보

|          구분          |   설명    |
|:--------------------:|:-------:|
|       **Port**       | `12300` |
|   **Context-Path**   |  `fxs`  |
| **Component Number** |  `210`  |

---

## 프로젝트 상세 기능

### 외화 계좌 정보 관리

- 외화 거래를 위한 다양한 종류의 외화 계좌를 구분하여 관리
- 외화 계좌 별 `CRUD` API 제공

|      구분       | 설명                      |       비고       |
|:-------------:|-------------------------|:--------------:|
| **외화 예치금 계좌** | 매입한 외화 입/출금 계좌          |  `FX_DEPOSIT`  |
| **외화 매입처 계좌** | 외화 매입 가능한 은행/증권사 계좌     | `FX_PURCHASER` |
| **MTO 펀딩 계좌** | 해외 송금 서비스를 위한 MTO 펀딩 계좌 | `MTO_FUNDING`  |

### 외화 계좌 거래 관리

- 외화 계좌 별 입금/출금 거래 트랜잭션 관리
- 외화 계좌 입출금 거래 내역 관리

### 환율 정보 관리

- OpenAPI 활용한 환율 조회 및 정보 관리
- 일별 환율 정보 이력 관리
- 일별 환율 정보 Kafka 메시징 처리

> [한국수출입음행 환율 조회 OpenAPI](https://www.koreaexim.go.kr/ir/HPHKIR020M01?apino=2&viewtype=C&searchselect=&searchword=)
> - 일 1000회 제한 기준 : 86.4초 단위로 하루 최대 1000회 요청 처리
> - API 인증키 관리
>   - `RESULT: "3"` 응답인 경우, API 인증키 갱신

### 외화 계좌 거래 정산 관리

- MTO 펀딩 외화 입출금(매입/매도) 거래 대사 처리
- 해외 송금 외화 입출금 거래 대사 처리

---

## 프로젝트 기록 문서

### [FXS 프로젝트 기록 문서](./deploy/docs/project_record_docs)

- `FXS` 프로젝트 구성/개발 과정 중 신규 기슬 스펙 적용 및 트러블 슈팅 기록

---
