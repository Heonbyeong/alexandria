## Observer Pattern
### 서론
객체의 상태 변화에 따라 다른 객체들이 자동으로 통지되고, 이에 따라 행동하는 패턴이다. 일대다(one-to-many) 관계를 정의하여, 
한 객체의 상태 변화가 한 개 이상의 객체에게 통보되어야 하는 경우에 유용하다.

### 핵심
- 주제(Subject): 상태 변화를 통지할 객체. 주제는 옵저버 객체들을 관리하며, 상태 변화가 발생하면 모든 옵저버들에게 통지한다.
- 옵저버(Observer): 주제의 상태 변화를 통지받는 객체. 옵저버는 주제에 등록되어 상태 변화를 감지하고, 이에 따라 동작한다.
---
Subject (주제) : 옵저버를 등록하고 제거하는 메서드를 포함. 상태 변화를 통지하기 위해 등록된 옵저버들을 관리한다.

Observer (옵저버) : 주제의 상태 변화에 따라 업데이트 되는 메서드를 정의한다.

ConcreteSubject : 주제의 구현체. 상태를 저장하고, 상태 변화가 발생하면 모든 등록된 옵저버들에게 통지한다.

ConcreteObserver : 옵저버의 구현체. 주제의 상태 변화에 따라 업데이트 메서드를 실행한다.

```kotlin
// Subject interface
interface Subject {
    fun registerObserver(observer: Observer)
    fun unregisterObserver(observer: Observer)
    fun notifyObservers()
}

// Observer interface
interface Observer {
    fun update(state: String)
}

// ConcreteSubject class
class ConcreteSubject : Subject {
    private val observers = mutableListOf<Observer>()
    private var state: String = ""

    fun setState(newState: String) {
        state = newState
        notifyObservers()
    }

    override fun registerObserver(observer: Observer) {
        observers.add(observer)
    }

    override fun unregisterObserver(observer: Observer) {
        observers.remove(observer)
    }

    override fun notifyObservers() {
        for (observer in observers) {
            observer.update(state)
        }
    }
}

// ConcreteObserver class
class ConcreteObserver(private val name: String) : Observer {
    private var observerState: String = ""

    override fun update(state: String) {
        observerState = state
        println("Observer $name updated with state: $observerState")
    }
}

// Usage
fun main() {
    val subject = ConcreteSubject()

    val observer1 = ConcreteObserver("Observer1")
    val observer2 = ConcreteObserver("Observer2")

    subject.registerObserver(observer1)
    subject.registerObserver(observer2)

    subject.setState("State 1")
    subject.setState("State 2")

    subject.unregisterObserver(observer1)
    subject.setState("State 3")
}

```
---
### 날씨 알림 시스템 구현해보기
### 요구사항
- 날씨 데이터를 관리하는 WeatherData에는 온도(temperature), 습도(humidity), 기압(pressure)의 데이터를 가지고 있습니다.
- 옵저버의 구현체는 ```Display``` 클래스이며, 날씨 정보가 업데이트될 때 해당 정보를 출력합니다.
- 주제의 구현체는 ```SubjectImpl``` 클래스이며, 옵저버들을 등록, 해제, 알림 작업을 수행합니다.
### 인터페이스
```kotlin
// Subject interface
interface Subject {
    fun registerObserver(observer: Observer)
    fun unregisterObserver(observer: Observer)
    fun notifyObservers()
}

// Observer interface
interface Observer {
    fun update(temperature: Float, humidity: Float, pressure: Float)
}
```

