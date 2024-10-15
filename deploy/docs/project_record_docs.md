# FXS 프로젝트 기록 문서

## Agenda

- Spring Boot v3.x 버전 업그레이드 관련 정리
- `Kotest` 라이브러리 적용

---

### Spring Boot v3.x 버전 업그레이드 관련 정리 👍

#### Spring Boot v3.2.3 버전 적용을 위해 필요한 항목

- `OpenJDK 21` 업그레이드!
- `Gradle v8.6` 업그레이드!
    - `io.spring.dependency-management`플러그인 `v1.1.4` 업그레이드!
    - `com.gorylenko.gradle-git-properties` 플러그인 `v2.4.1` 업그레이드! *(선택사항)*

> ##### `Gradle v8.5` 의 문제점 😈
> - `io.spring.dependency-management` 플러그인에서 아래와 같은 `:detachedConfiguration1` task 에러 발생
> ```
> Unexpected exception while resolving Gradle distribution sources: Could not resolve all files for configuration ':detachedConfiguration1'.
> org.gradle.api.internal.artifacts.ivyservice.DefaultLenientConfiguration$ArtifactResolveException: Could not resolve all files for configuration ':detachedConfiguration1'.
> ...
> ```
> - `Gradle v8.5` 에서의 `io.spring.dependency-management` 이슈 확인되어 플러그인 자체 `disable` 처리 필요
> - 플러그인 비활성화 처리 없이 `Gradle v8.6` 버전 업그레이드를 통해서 해결

---

#### `gradle-git-properties` 플러그인 신규 버전 적용

- `gradle-git-properties` 플러그인 버전 업그레이드로 `Grgit` 클래스 `deprecated` 되어 수정 필요
- `gradle-git-properties` 플러그인 `v2.x` 버전부터 `git.properties` 파일 생성 및 `Git` 관련 정보 프로퍼티 처리
- 별도 프로퍼티 파일 생성 없이 `Java Configuration` 클래스로 `Git` 정보 수집 및 `Actuator Health` 처리

```kotlin
package com.konai.fxs.infra.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.actuate.health.Status
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component

@PropertySource(value = ["classpath:/git.properties"])
@Component(value = "application")
class HealthEndpointConfig : HealthIndicator {

    @Value("\${git.commit.id.abbrev}")
    lateinit var hash: String

    @Value("\${git.build.version}")
    lateinit var version: String

    @Value("\${git.build.time}")
    lateinit var buildDate: String

    override fun health(): Health {
        return Health
            .Builder(Status.UP, mapOf("version" to this.version, "hash" to this.hash, "build-date" to this.buildDate))
            .build()
    }
}
```

> ##### `git.properties` 적용 시 주의 사항 ❗
> - 프로젝트 `build` 시, `resources` 디렉토리에 `git.properties` 파일 생성 확인 가능하나,
> - Spring Boot 프로젝트 실행 시, 프로젝트 `resources` 디렉토리에는 물리 파일 생성되진 않기 때문에,
> - `@PropertySource(value = ["classpath:/git.properties"])` 설정을 통해서 강제로 `git.properties` 주입 처리

##### Git Custom Properties 생성

- `gradle-git-properties` 플러그인 기본 프로퍼티 외 Custom Properties 추가

```kotlin
gitProperties {
    val primary = "${project.property("version.primary")}"
    val major = "${project.property("version.major")}"
    val minor = "${project.property("version.minor")}"

    val buildVersion = listOf(primary, major, minor).joinToString(".")
    val buildTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    println("buildVersion = $buildVersion")
    println("buildDateTime = $buildTime")

    customProperty("git.build.version", buildVersion)
    customProperty("git.build.time", buildTime)
}
```

---

### `Kotest` 라이브러리 적용 👍

- **`Kotlin` 코틀린스러운** 테스트 코드 작성을 위한 `Kotest` 라이브러리 적용
- `BDD` 와 같은 테스크 코드 작성 가능
- 확장 함수, 중위 함수, `Kotlin DSL` 등 `Kotlin` 언어가 가진 언어 특화성 보장
- 문제점: Create New Test가 안됨.

#### `Kotest` Intellij Plugin 설치

- 파일 위치 : /data/home/map/temp/00_setup/02_plugins/intellij/kotest-plugin-intellij-1.3.74-IC-2023.3.zip
- 필요성 : 각 테스트에 대한 실행 아이콘, 테스트 탐색을 위한 도구 창, 중복된 테스트 강조 표시 등 제공
- 한계점 : Create New Test 불가

---
