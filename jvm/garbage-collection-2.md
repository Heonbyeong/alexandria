# garbage-collection-2
지난 시간에는 JVM 메모리 구조, 자료구조 힙, GC에 대하여 알아보았습니다. 이번에는 GC의 동작방식에 대해 알아보도록 하겠습니다.

## GC는 어떻게 동작할까
GC는 사용하지 않는 객체를 찾아 메모리를 해제하여 메모리 누수를 방지한다고 했습니다. 근데 사용하지 않는 객체는 어떻게 찾는 걸까요?

### 가비지 컬렉션 대상

- ```Reachable``` : 객체가 참조되고 있는 상태
- ```Unreachable``` : 객체가 참조되고 있지 않은 상태 (GC의 대상)

두 상태를 통해 사용 중인 객체와 사용하지 않는 객체를 구분합니다.

<img width="757" alt="스크린샷 2024-09-02 오후 9 31 33" src="https://github.com/user-attachments/assets/e94c23f5-432d-43aa-9eb8-0b8598dd0b4d">

JVM 메모리에서는 객체는 ```Heap``` 영역에 할당되고, ```Method Area``` / ```Stack``` 영역에서 객체의 주소를 참조하는 형식으로 구성됩니다. 객체의 메모리 주소를 가지고 있는 변수가 삭제된다면,
참조하고 있지 않은 객체```(2)```가 발생합니다. 이런 객체들을 주기적으로 가비지 컬렉터가 찾아 제거하는 방식입니다.

----

### Stop-The-World
가비지 컬렉션을 자세히 들어가기 전, 알아야 할 것이 있습니다. 바로 ```STW(Stop-The-World)```입니다.
GC를 수행하기 위해 JVM이 애플리케이션을 멈추는 현상을 의미합니다. 이 때 GC 관련 스레드 외에 모든 스레드는 멈추기 때문에 사용자 경험을 나쁘게 만들 수 있습니다.
<img width="373" alt="스크린샷 2024-09-02 오후 9 55 40" src="https://github.com/user-attachments/assets/b8f7827b-8fc4-470c-8d49-bf194e12955f">

----

### 참조 방식
- 힙 영역 내의 다른 객체에 의한 참조
- **스택 영역의 메서드 내에서 실행하는 지역변수, 파라미터, 연산 작업 중 피연산자에 의한 참조**
- **메소드 영역의 상수 풀, 정적 변수에 의한 참조**
- **메모리에 남아있는 Native 메서드로 넘겨진 객체에서 참조**

하위 3개의 참조 방식은 ```Root Set```으로 ```Reachability (객체의 참조 유형 정도)```를 판가름하는 기준이 됩니다. ```Root Set```으로 부터 참조가 있다면 ```Reachable``` 로 판단합니다.

----

### 가비지 컬렉션의 청소 방법

#### Mark and Sweep
가비지 컬렉션이 동작하는 기초적인 알고리즘 입니다. 가비지 컬렉션의 대상이 되는 객체를 식별```(Mark)```하고 제거```(Sweep)```, 제거되어 파편화된 메모리 영역을 앞에서부터 채우는```(Compaction)```을 수행합니다.

<img width="729" alt="스크린샷 2024-09-02 오후 9 50 06" src="https://github.com/user-attachments/assets/b8fba7d7-bae5-4b2a-a570-5f9cf730d761">

<img width="300" alt="스크린샷 2024-09-02 오후 9 50 25" src="https://github.com/user-attachments/assets/3283e3a1-f46b-4c9c-9a04-02bc7ef01037">
<img width="300" alt="스크린샷 2024-09-02 오후 9 50 32" src="https://github.com/user-attachments/assets/de80b217-1c1b-4466-b18d-586383196768">

1. 순회를 통해 연결된 객체를 찾아 ```Reachable``` 상태의 객체를 찾아 마킹합니다.
2. ```Unreacahble``` 객체들을 ```Heap```에서 제거합니다.
3. ```Sweep``` 후, 분산된 객체들을 ```Heap``` 영역의 앞에서부터 채워나갑니다.

----

### Heap 메모리 구조
Heap 영역은 두 가지를 전제로 설계되었습니다.

1. 대부분의 객체는 금방 Unreachable 상태가 된다.
2. 오래된 객체에서 새로운 객체로의 참조는 아주 적게 존재한다.

**객체는 대부분 일회성이며, 메모리에 오랫동안 남아있는 경우는 드물다는 것**을 알 수 있습니다.
이 Heap 메모리는 ```Young Generation```, ```Old Generation``` 영역으로 나뉩니다.

<img width="799" alt="스크린샷 2024-09-02 오후 10 31 37" src="https://github.com/user-attachments/assets/a70d5540-2a6d-4403-bfcc-a7a170e20095">

#### Young Generation
- 새롭게 생성된 객체가 할당되는 영역입니다.
- 해당 영역에서는 가비지 컬렉션이 자주 일어납니다. (빠르게 생성 및 삭제되기 때문)
- **해당 영역에 대한 가비지 컬렉션을 ```Minor GC(Old Generation에 비해 상대적으로 메모리 공간이 작기 때문에 가비지 컬렉션에 보다 적은 시간이 소비된다.)```라고 합니다.**

```Young Generation```은 다시 세 가지 영역```(Eden, survivor 0, survivor 1)```으로 분할됩니다.
<img width="783" alt="스크린샷 2024-09-02 오후 10 37 05" src="https://github.com/user-attachments/assets/fbc8c466-bba1-463d-971d-0d95d2d7005e">

##### Eden 
- 객체가 처음 생성될 때 할당되는 공간입니다.
- 해당 영역이 가득 차면, ```Minor GC```가 발생하며, 이때 살아남은 객체는 ```Survivor``` 영역으로 이동합니다.

##### Survivor 0 / Survivor 1
- Eden 영역에서 살아남은 객체들이 머무는 공간입니다.
- 두 개의 ```Survivor``` 영역은 상호 교대하며 사용됩니다. (== 둘 중 하나의 영역은 무조건 비어있음)

#### Minor GC 과정
1. ```Eden``` 영역과 활성화된 ```Survivor``` 영역의 객체 중에서 참조되는 객체들은 반대쪽 ```Survivor``` 영역으로 복사됩니다.
2. ```Unreachable``` 객체들은 가비지로 간주되어 메모리에서 삭제됩니다.

(Survivor 영역 간의 이동 시 객체의 ```age(객체가 살아남은 횟수를 의미하며, 임계값에 다다르면 Old 영역으로의 이동 여부를 결정합니다.)```값이 증가합니다.
<img width="575" alt="스크린샷 2024-09-02 오후 10 46 30" src="https://github.com/user-attachments/assets/4c0d9d4b-726f-4b4a-99fb-6e286eb182cb">

#### Old Generation
- 특정 횟수의 ```Minor GC```를 거친 후에도 살아남은 객체들이 존재하는 영역입니다.
- **해당 영역에 대한 가비지 컬렉션을 ```Major GC(Full GC)```라고 합니다.**
- ```age``` 값이 임계값을 넘을 경우 해당 영역으로 이동하게 되는데, 이를 ```Promotion```이라고 합니다.
- 해당 영역이 가득 차면 ```Major GC```가 발생합니다.

<img width="678" alt="스크린샷 2024-09-02 오후 11 05 28" src="https://github.com/user-attachments/assets/d013c7f0-eb0c-4c1d-9300-05988e604f3a">

※ Young Generation 보다 상대적으로 큰 메모리 공간을 가지고 있어, GC에 많은 시간```(보통 Minor GC에 10배 이상의 시간)```이 걸리게 된다. 따라서 앞전에 설명한 ```Stop-The-World```가 발생한다. ```STW```의 시간을 줄이는 것을
```GC 튜닝```이라고 한다.
