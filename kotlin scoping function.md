# Kotlin Scoping Functions

## 1. `apply` 수신 객체의 `프로퍼티`만 사용

```kotlin
val peter = Person().apply {
    // 객체 초기화
    name = "Peter"
    age = 18
}
```

## 2. `run` `계산` 또는 여러 지역변수 범위 제한

```kotlin
val inserted: Boolean = run {
    // person과 personDao의 범위 제한
    val person: Person = getPerson()
    val personDao: PersonDao = getPersonDao()

    personDao.insert(person)
}

fun printAge(person: Person) = person.run {
    // person을 수신객체로 변환하여 age값 사용
    print(age)
}
```

## 3. `with` 결과가 필요하지 않은 경우

```kotlin
val person: Person = getPerson()
with(person) {
    print(name)
    print(age)
}
```

## 4. `let` `NotNull` 또는 단일 지역변수 범위 제한

```kotlin
getNullablePerson()?.let {
    // If NotNull
    promote(it)
}

val driverLicense: License? = getNullablePerson()?.let {
    // Nullable 객체를 다른 Nullable 객체로 변환
    licenseService.getDriversLicense(it)
}

val person: Person = getPerson()
getPersonDao().let { dao ->
    // 단일 지역변수(dao)의 범위 제한
    dao.insert(person)
}
```

## 5. `also` 객체의 사이드이펙트 `확인`, 데이터 유효성 `검사`

```kotlin
class Book(author: Person) {
    val author = author.also {
        // 유효성 검사
        requireNotNull(it.age)
        print(it.name)
    }
}
```

## 6. `중첩` / `호출 체인`

- apply, run, with를 중첩할 경우 this를 혼동하기 쉬워져 삼가는 것이 좋다.

- let, also을 중첩할 경우 it 대신 명시적 이름을 사용하는 것이 좋다.

- 다음과 같이 `호출 체인`으로 목적에 맞게 사용할 경우 가독성을 향상시킬 수 있다.

```kotlin
private fun insert(user: User) = sqlBuilder().apply {
    append("INSERT INTO user (email, name, aget) VALUES ")
    append("(?", user.email)
    append(",?", user.name)
    append(",?)", user.age)
}.also {
    print("Executing SQL update: $it.")
}.run {
    jdbc.update(this) > 0
}
```

> 출처 : [코틀린 의 apply, run, with, let, also 은 언제 사용하는가?](https://medium.com/@limgyumin/%EC%BD%94%ED%8B%80%EB%A6%B0-%EC%9D%98-apply-with-let-also-run-%EC%9D%80-%EC%96%B8%EC%A0%9C-%EC%82%AC%EC%9A%A9%ED%95%98%EB%8A%94%EA%B0%80-4a517292df29)
