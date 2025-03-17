# Kotlin 반공변성(in), 공변성(out)에 대하여

## 개요

> 코틀린으로 개발을 하다보면 `in`/`out` 이라는 키워드를 한 번쯤은 접했을 겁니다. 뭔가 한 번쯤은 제네릭을 사용하는 부분에서 봤던 기억이 있는 거 같은데요. 이 키워드는 어떤 역할을 하는 건지 알아보겠습니다.
(이후 이펙티브 코틀린에서 이어지는 내용입니다)
> 

```kotlin
public interface List<out E> : Collection<E> {
    override val size: Int
    override fun isEmpty(): Boolean
    override fun contains(element: @UnsafeVariance E): Boolean
    override fun iterator(): Iterator<E>
    override fun containsAll(elements: Collection<@UnsafeVariance E>): Boolean
    public operator fun get(index: Int): E
    public fun indexOf(element: @UnsafeVariance E): Int
    public fun lastIndexOf(element: @UnsafeVariance E): Int
    public fun listIterator(): ListIterator<E>
    public fun listIterator(index: Int): ListIterator<E>
    public fun subList(fromIndex: Int, toIndex: Int): List<E>
}
```

## 변성 - Variance

> 변성이란, 기저 타입(base type)이 같고 타입 인자(type argument)가 다른 제네릭 타입간의 계층 관계를 설명하는 개념입니다. (기본 타입 사이의 상속 관계가 제네릭 타입에서는 어떻게 적용되는지 다루는 개념)
> 

![image](https://github.com/user-attachments/assets/ea5ff157-daf2-4f1c-aead-89e86d79be6b)

기저 타입과 타입 인자

여기서 한 가지 물음이 있습니다. `List<String>`은 `List<Any>`의 하위 타입일까요? 이 물음에 답하기 위해 변성이라는 개념이 등장했습니다.

### class ≒ type

클래스와 타입은 비슷한듯 미묘하게 다른 차이를 가지고 있습니다. `class → 객체를 생성하기 위한 설계도`

`type → 변수에 담을 수 있는 값의 집합` . String은 클래스입니다. 그러나 아래와 같은 경우에는 클래스와 타입은 동일합니다.

```kotlin
var str: String
```

코틀린에서 일반적인 타입이라면 `nullable` 타입을 포함하여 두 가지의 타입만 존재할 수 있습니다. `String`, `String?` 처럼 말이죠. 그렇다면 제네릭의 경우는 어떨까요? `List<String>`, `List<String?>`, `List<List<String>>`처럼 무수히 많은 타입을 만들어 낼 수 있습니다.

### 하위 타입

> 타입 A의 값이 필요한 모든 위치에 다른 타입 B의 값을 넣어도 문제가 없는 경우, **B는 A의 하위 타입**이라고 볼 수 있습니다.
> 

```kotlin
open class Animal

class Dog : Animal()
...
fun run(animal: Animal)
...

val dog = Dog()
run(dog) // Dog는 Animal의 하위타입 입니다.
```

앞서 나온 질문을 다시 한 번 하겠습니다. `List<String>`은 `List<Any>`의 하위 타입일까요? 다시 말해, List<Any> 타입에 List<String>을 전달해도 문제가 발생하지 않을까요 ?

문제가 발생하지 않습니다. 우선 String은 Any의 하위 타입입니다. (java의 Object와 비슷한 역할) Immutable List의 경우 요소의 변경이 일어나지 않으므로 타입 불일치가 발생할 수 없습니다. 따라서 List<String>은 List<Any>의 하위 타입입니다. 

![image](https://github.com/user-attachments/assets/bb66fedd-5b76-4f9f-9ed9-ee1d232857df)

리스트의 하위 타입

## 무공변(invariant)

MutableList의 경우에는 요소가 변경될 가능성이 있기 때문에 불가능합니다. ****이는 **무공변(invariant)**을 의미합니다. (in/out 키워드가 없다면 기본적으로 제네릭은 무공변이 됩니다.)

> 무공변 : 제네릭 타입 간에 어떤 하위 타입 관계도 성립하지 않는 경우
> 

```kotlin
class MutableList<T>

open class Animal
class Dog : Animal()

fun main() {
		val dogList: MutableList<Dog> = mutableListOf()
		val animalList: MutableList<Animal> = dogList // complie error
}
```

## 공변(Covariance)

> A가 B의 하위 타입일 때 제네릭 클래스 A가, 제네릭 클래스 B의 하위 타입이 되는 경우를 공변이라고 합니다. 
→ 제네릭 타입의 상속 관계가 원본 타입의 상속 관계와 같은 방향으로 흘러가는 것
> 

코틀린에서는 이 공변성을 out 키워드로 표시합니다.

```kotlin
interface List<out T> // 공변성 표시 (out)

open class Animal
class Dog : Animal()

fun main() {
		val dogList: List<Dog> = listOf(Dog())
		val animalList: List<Animal> = dogList // 아무런 문제가 발생하지 않습니다.
}
```

이런 공변성에는 다음과 같은 제약사항이 있습니다.

- 가변 데이터의 경우엔 사용할 수 없음 (타입 안정성을 보장할 수 없기 때문)
- 공변성이 선언된 타입 파라미터는 반환 타입의 위치에서만 사용할 수 있음

```kotlin
interface Producer<out T> {
		fun set(value: T) // 반환 타입이 아니더라도 허용된다면
}

open class Animal
class Dog : Animal()
class Cat : Animal()

fun main() {
		val dogProducer: Producer<Dog> = ...
		val animalProducer: Producer<Animal> = dogProducer // 공변성으로 인해 가능
		animalProducer.set(Cat()) // compile error, Dog를 기대하는 곳에 Cat이 들어감
}
```

## 반공변(Contravariance)

> A가 B의 하위 타입일 때, 제네릭 클래스 A가 제네릭 클래스 B의 상위 타입이 되는 경우를 반공변이라고 합니다. 
→ 제네릭 타입의 상속 관계가 원본 타입의 상속 관계와 반대 방향으로 흘러가는 것
> 

코틀린에서는 이 반공변성을 in 키워드로 표시합니다.

```kotlin
interface Comparator<in T> {
    fun compare(a: T, b: T): Int
}

open class Animal
class Dog : Animal()

fun main() {
    // Animal을 비교할 수 있는 비교자는 Dog도 비교할 수 있음
    val animalComparator: Comparator<Animal> = object : Comparator<Animal> {
        override fun compare(a: Animal, b: Animal): Int = 0
    }
    
    val dogComparator: Comparator<Dog> = animalComparator // 반공변성으로 인해 가능
}
```

- 반공변 추가 예제 코드
    
    ```kotlin
    open class Animal {
        open fun makeSound() = "동물 소리"
    }
    class Dog : Animal() {
        override fun makeSound() = "멍멍"
    }
    
    // 동물을 돌보는 인터페이스
    interface AnimalCare<in T> {
        fun feed(animal: T)
        fun groom(animal: T)
    }
    
    // 모든 동물을 돌볼 수 있는 케어테이커
    val animalCareService = object : AnimalCare<Animal> {
        override fun feed(animal: Animal) {
            println("동물에게 먹이를 줍니다")
        }
        override fun groom(animal: Animal) {
            println("동물을 그루밍합니다")
        }
    }
    
    fun main() {
        // 모든 동물을 돌볼 수 있는 케어테이커는 당연히 개도 돌볼 수 있음
        val dogCareService: AnimalCare<Dog> = animalCareService
        val myDog = Dog()
        dogCareService.feed(myDog) // "동물에게 먹이를 줍니다"
        dogCareService.groom(myDog) // "동물을 그루밍합니다"
    }
    ```
    

이런 반공변성에는 다음과 같은 제약사항이 있습니다.

- 반공변성이 선언된 타입 파라미터는 파라미터의 위치에서만 사용할 수 있음

```kotlin
interface Consumer<in T> {
    fun consume(value: T) // 파라미터의 위치이기에 문제 없음
    fun produce(): T // 파라미터가 아니더라도 허용된다면
}

open class Animal
class Dog : Animal()

fun main() {
    val animalConsumer: Consumer<Animal> = ...
    val dogConsumer: Consumer<Dog> = animalConsumer // 반공변성으로 인해 가능
    
    // animalConsumer는 Animal을 반환하는데, 이를 Dog 타입으로 받으려고 하면 타입 안정성이 깨짐
    val dog: Dog = dogConsumer.produce() // compile error
}
```

## in/out 키워드의 진실된 의도

### out 키워드에 대한 의미

```kotlin
class Producer<out T>
```

- T 타입의 값을 내보낸다(생산)는 의미
- T가 반환 타입으로 쓰인다면, T는 나가는 (out) 형태이므로 out 위치에 있다고 할 수 있음 
→ 데이터가 밖으로 나가는 방향

### in 키워드에 대한 의미

```kotlin
class Consumer<in T>
```

- T 타입의 값을 받는다(소비)는 의미
- T가 파라미터 타입으로 쓰인다면, T는 들어오는 (in) 형태이므로 in 위치에 있다고 할 수 있음
→ 데이터가 안으로 들어오는 방향
