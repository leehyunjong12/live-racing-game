# 🎨 Sketch Race (스케치 레이스)
> **우테코 2주차 과제를 확장한 Spring Boot와 WebSocket을 활용한 실시간 그래프 기반 레이싱 & 베팅 게임**

![Java](https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot)
![WebSocket](https://img.shields.io/badge/WebSocket-Realtime-orange?style=for-the-badge)
![JPA](https://img.shields.io/badge/Spring_Data_JPA-Hibernate-59666C?style=for-the-badge&logo=hibernate)
![H2](https://img.shields.io/badge/Database-H2-blue?style=for-the-badge)

<br>

## 📖 프로젝트 소개 (Overview)

**Sketch Rush**는 단순한 1차원 선형 경주가 아닌, **복잡한 그래프(Graph) 자료구조** 위에서 펼쳐지는 실시간 레이싱 시뮬레이션 게임입니다.

**"손으로 그린 듯한(Sketch)"** 감성적인 UI 뒤에는, **실시간 양방향 통신**, **금융 트랜잭션의 무결성**, **객체지향적 설계 원칙(SOLID)** 이 견고하게 구축되어 있습니다.

사용자는 5분마다 자동으로 열리는 경주에 참여하여 배당금을 획득할 수 있습니다.

<br>

## 📸 주요 기능 및 화면 (Screenshots)

| 메인 로비 (대기/로그인) | 경기 10초전 (모든 버튼 비활성화) |
| :---: |:--------------------:|
| ![Lobby](https://github.com/user-attachments/assets/e3d0d677-7a5c-4de2-9c1c-f4c226fad0be) | ![Racing](https://github.com/user-attachments/assets/12a11415-5f1f-46da-b229-4e9f431abd0a) |
| *5분 자동 타이머와 참가 신청* | *모든 모달이 닫히고 버튼 비활성화* |

|         실시간 경주 (그래프 이동)          |                                     우승자 발표 (모달)                                      |
|:--------------------------------:|:------------------------------------------------------------------------------------:|
| ![Winner](https://private-user-images.githubusercontent.com/169887906/517955854-b28c1190-4281-423e-a173-25ae57cc8cd6.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM5NjE3NjYsIm5iZiI6MTc2Mzk2MTQ2NiwicGF0aCI6Ii8xNjk4ODc5MDYvNTE3OTU1ODU0LWIyOGMxMTkwLTQyODEtNDIzZS1hMTczLTI1YWU1N2NjOGNkNi5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTI0JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTEyNFQwNTE3NDZaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1lNzdkZWNiNDQ5ZTcwYWI1YjVkMzQ4YjI2NTAzODYzMTc0NjBiZTBlMzhkM2E5ODY5NDFmOWM2ZDY5M2Q2ZjE4JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.6Vv4Drhxfdxv274DMtV6AHdt9NXbyuBPTUaJG_eSG8w) | ![Log](https://private-user-images.githubusercontent.com/169887906/517962269-01b44238-b124-4e0a-821b-6389ad9dbccf.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjM5NjI1NzQsIm5iZiI6MTc2Mzk2MjI3NCwicGF0aCI6Ii8xNjk4ODc5MDYvNTE3OTYyMjY5LTAxYjQ0MjM4LWIxMjQtNGUwYS04MjFiLTYzODlhZDlkYmNjZi5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUxMTI0JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTEyNFQwNTMxMTRaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT02ZWM5ZDljZGJlZTgwZGYxYTFhMDg4YTM2MGMyOTI1NDA2ZGM0Mjg5M2VhNTFmMTYyMTAwY2FjMmM2YTFiZGFmJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.cIKD7XM4nosD_n1YhPg9OwbT_nfMcLNZ0zocDEPTC18) |
|      *노드와 간선을 따라 이동하는 자동차*       |                                 *Confetti 효과와 결과 화면*                                 |

<br>

## 🕹️ 게임 방법 (How to Play)

**Sketch Rush**는 5분마다 자동으로 열리는 **실시간 참여형 레이싱 게임**입니다.

### 1. 참여 등급 (Access Level)
| 구분 | 👤 게스트 (비로그인) | 🏎️ 레이서 (로그인) |
| :--- | :---: | :---: |
| **경기 관전** | ✅ 가능 | ✅ 가능 |
| **실시간 로그 확인** | ❌ 불가 | ✅ **가능** |
| **잔액 충전** | ❌ 불가 | ✅ **가능** |
| **자동차 등록** | ❌ 불가 | ✅ **가능** |

### 2. 경기 사이클 (Time Flow)
서버 스케줄러에 의해 **매 5분 정각**마다 경기가 시작됩니다.

1.  **참가 접수 (`00:00 ~ 04:49`)**: 로그인 후 자동차를 등록(`50,000원/대`)할 수 있습니다. 총 상금은 사용자들의 등록 비용 합이며 실시간으로 누적되어 보여줍니다.
2.  **마감 임박 (`04:50 ~ 04:59`)**: **🚫 Lockdown!** 모든 등록과 충전이 차단되고, 트랙이 초기화됩니다.
3.  **카운트다운 (`Start - 3s`)**: 화면 중앙에 **3, 2, 1** 애니메이션이 출력됩니다.
4.  **레이스 시작 (`05:00`)**: 자동차들이 그래프 노드를 따라 이동하며 경주를 펼칩니다.

### 3. 인게임 (In-Game)
* **이동:** 자동차는 노드(Node)와 간선(Edge)을 따라 이동합니다.
* **구별:** 본인 자동차만 노란색으로 표시됩니다.
* **특수 이벤트:** 본인 자동차가 감옥, -2로 이동, 출발점이동 등 특수 노드에 도착하면, **우측 사이드바 로그**에 실시간으로 상태가 중계됩니다.
  * 🔴 `JAIL`: 감옥 도착 (2턴 정지)
  * 🟠 `MOVE_BACK_NODE`: 현재 노드 -2 위치로 이동
  * 🔵 `MOVE_TO_START`: 시작점 복귀 등

### 4. 승리 및 보상 (Rewards)
* **우승자 독식:** 가장 먼저 결승선(Node 30)에 도착한 사용자가 **누적된 총 상금**을 획득합니다.
* **공동 우승:** 여러 명이 동시에 들어오면 상금을 **N등분**하여 나눠 갖습니다.
* **🤖 Admin 단독 우승:** 만약 운영자 봇(`Admin_Bot`)이 단독으로 우승하면, 상금은 누구에게도 지급되지 않고 **다음 판으로 이월(Carry Over)** 됩니다.

## 🏛️ 시스템 아키텍처 (Architecture)

**철저한 계층 분리(Layered Architecture)** 와 **단일 책임 원칙(SRP)** 을 준수하여 설계했습니다.

```bash
src/main/java/com/example/racing_game
│
├── config                          # [설정] 스프링 빈, 보안, 웹소켓
│   ├── AppConfig.java              # 수동 Bean 등록 (GameRuleEngine, Random 등)
│   ├── SecurityConfig.java         # 보안 필터, 암호화, 인가 설정
│
├── controller                      # [컨트롤러] 외부 요청(HTTP/WS) 진입점
│   ├── AuthController.java         # 로그인, 회원가입, 내 정보 API
│   ├── CarController.java          # 자동차 등록 API
│   ├── PaymentController.java      # 잔액 충전/조회 API
│   ├── PrizeController.java        # 상금 현황 조회 API
│   └── RaceBroadcastHandler.java   # 웹소켓 핸들러 (5분 스케줄러 포함)
│
├── service                         # [서비스] 비즈니스 로직 & 트랜잭션
│   ├── AuthService.java            # 회원가입, 로그인 로직 (비번 암호화)
│   ├── CarService.java             # 자동차 등록 (참가비 차감, Pot 누적)
│   ├── JpaUserDetailsService.java  # Spring Security 인증 로직
│   ├── PrizeService.java           # 우승자 상금 분배, Pot 초기화, DB 정리
│   ├── RaceService.java            # 레이싱 게임 진행, 상태 관리, 중계
│   └── UserService.java            # 잔액 충전 로직
│
├── repository                      # [레포지토리] DB 데이터 접근 (JPA)
│   ├── PrizePoolRepository.java
│   ├── UserCarRepository.java
│   └── UserRepository.java
│
├── domain                          # [도메인] 핵심 객체 & 게임 규칙
│   │   # -- JPA 엔티티 (DB 테이블) --
│   ├── User.java                   # 사용자 계정 (잔액 포함)
│   ├── UserCar.java                # 등록된 차량 정보
│   ├── PrizePool.java              # 총 상금(Pot)
│   │
│   │   # -- 게임 로직 객체 (POJO) --
│   ├── Car.java                    # (인게임용) 실시간 경주 차량 객체
│   ├── GameRuleEngine.java         # 이동/페널티/특수노드 계산기
│   ├── MapDataStorage.java         # 맵 데이터 원본 (SSoT)
│   ├── MoveStrategy.java           # 이동 전략 인터페이스
│   ├── RandomMoveStrategy.java     # 랜덤 이동 전략 구현체
│   ├── TileType.java               # 노드 타입 Enum (JAIL, SLIDE...)
│   └── TrackLayout.java            # 맵 시각화 데이터 생성기
│
└── dto                             # [DTO] 데이터 전송 객체
    ├── CarRegistrationRequest.java
    ├── ChargeRequest.java
    ├── LoginRequest.java
    ├── RegisterRequest.java
    ├── MapLayoutDto.java
    ├── MapNode.java
    └── RuleResult.java

src/main/resources
├── application.properties          # DB 연결(H2), 포트, 로깅 설정
└── static                          # [프론트엔드] 정적 리소스
    ├── app.js                      # 게임 로직, 캔버스 렌더링, API 호출
    ├── index.html                  # 메인 화면 구조 (모달, 캔버스 포함)
    └── style.css                   # 스케치 테마 스타일링

```
## ⚙️ 개발 환경 및 요구사항 (Prerequisites)

이 프로젝트는 다음 환경에서 개발 및 테스트되었습니다. 원활한 실행을 위해 해당 버전 이상의 환경을 권장합니다.

* **JDK:** Java 21 이상 (필수)
* **Framework:** Spring Boot 3.4.x
* **Build Tool:** Gradle 8.x (Gradle Wrapper 포함)
* **Database:** H2 (In-memory / 별도 설치 불필요)
* **Browser:** Chrome, Edge, Safari 등 최신 브라우저 (HTML5 Canvas 및 WebSocket 지원 필수)

## ▶️ 실행 방법
```bash
# 1. Clone Repository
git clone https://github.com/leehyunjong12/live-racing-game.git

# 2. cd file
cd live-racing-game

# 3. Build & Run
./gradlew bootRun

#4 Access
웹 브라우저를 열고 http://localhost:8080 접속.
회원가입 후 로그인하면 5분마다 자동으로 열리는 레이스에 참여할 수 있습니다!
```

### 로컬 멀티플레이 테스트 방법 
베포 이전이라 서버 배포 없이, **내 컴퓨터(Localhost)** 에서 여러 명의 플레이어가 참가하는 상황을 시뮬레이션할 수 있습니다.

1. **크롬 브라우저(일반 탭)** 를 엽니다. -> `http://localhost:8080` 접속 (로그인: **User A**)
2. **크롬 시크릿 탭(Incognito)** 또는 **다른 브라우저(Edge, Safari)**를 엽니다. -> `http://localhost:8080` 접속 (로그인: **User B**)
3. 서로 다른 계정으로 로그인하면, 하나의 방에서 함께 경주하고 로그를 확인할 수 있습니다.

## 🚀 구현 과정

2주차 자동차 경주 미션을 Spring Boot, WebSocket 기반의 '실시간 그래프 기반 웹 애플리케이션'으로 확장하는 프로젝트

v1.0에서 '실시간 1D 플랫폼'을 구축

v2.0에서는 '그래프(Graph)' 기반의 복잡한 맵 로직과 '동적 맵 전송' 아키텍처를 구현

v3.0에서는 서비스 운영 및 금융 시스템 도입

---

### 🛠️ 기술 스택

* **Backend:** Java 21, Spring Boot, WebSocket, Spring Security, H2 Database
* **Frontend:** HTML5 'canvas', CSS, JavaScript
* **Build:** Gradle

---

### ✅ v1.0: 1D 실시간 플랫폼 (구현 완료)

> (v1.0은 1차원 `div` 이동 방식으로, v2.0부터는 `canvas` 기반으로 재구축)

**Backend**
- [x] Spring Boot 기반의 웹 서버 실행
- [x] WebSocket 엔드포인트 활성화
- [x] 클라이언트 접속 시 WebSocket 세션 연결 및 관리
- [x] 클라이언트가 보낸 `START` 메시지 수신 및 처리
- [x] 자동차들 random 값에 의한 경주 기능 구현
- [x] 경주 상태, 우승자 추출 JSON 문자열로 변환
- [x] 경주가 진행되는 동안, 연결된 모든 클라이언트에게 JSON 데이터 **실시간 방송(Broadcast)**

**Frontend**
- [x] 경기장 뼈대 및 CSS 스타일링
- [x] WebSocket 클라이언트로 서버 엔드포인트 접속
- [x] WebSocket 연결 성공 시 '경주 시작' 버튼 활성화
- [x] '경주 시작' 버튼 클릭 시, 서버로 `START` 메시지 전송
- [x] 서버로부터 실시간으로 방송되는 JSON 데이터 수신
- [x] 수신한 데이터를 기반으로 실시간 애니메이션 구현
- [x] 트랙 초기화 버튼 구현

---
### ✅ v2.0: 그래프(Graph) 맵 및 아키텍처 리팩토링 (구현 완료)

> v1의 한계를 극복하기 위해 v2.0에서는 아키텍처를 재설계하고 그래프(Graph) 기반의 맵을 도입

**Backend**
- [x] 그래프 맵 기능 구현 (갈림길)
- [x] 특수 칸 기능 구현(지름길,장애물,후진)
- [x] 맵 전송을 위한 DTO(Data Transfer Object) 설계
- [x] 클라이언트 접속 시 맵 레이아웃(좌표, 선)을 JSON으로 즉시 전송
- [x] '게임 규칙 엔진'이 '그래프' 기반의 다음 위치를 계산하도록 구현
- [x] 한 차라도 결승선에 도착하면 중단하고 우승자 반환 

**Frontend**
- [x] 렌더링 엔진을  `HTML <canvas>` 기반으로 교체
- [x] 서버로부터  JSON을 동적으로 다운로드하여 맵을 생성
- [x] `requestAnimationFrame`을 이용한 1초 60프레임 게임 루프 도입
- [x] 서버에서 받은 '노드 번호'를 맵 좌표의 값으로 변환하여 캔버스에 렌더링
- [x] 캔버스에 '연결 선'과 '노드'를 그려 '그래프 맵'  시각화
- [x] 맵 데이터가 수신되어야 '경주 시작' 버튼 활성화로 변경

---


### ✅ v2.0 수정사항: 몇몇 에러 처리 및 수정 (구현 완료)

**Backend**
- [x] 지름길 노드는 노드 연결 방식으로 충분히 구현 가능 -> 삭제
- [x] 모든 노드가 '다음 노드' 목록을 갖도록 '맵 데이터' 구조 변경
  - 현재 순차적인 방법은 노드 재배치 불가
- [x] 노드 구조 더 다양하게 배치
- [x] 노드 타입 '한턴 감옥', '뒤로 두칸' 무한 루프 발생 해결
  - '한턴 쉬기'는 기능적으로 해결
  - '뒤로 두칸'은 '확률적으로 현재 노드 번호 -2로 이동'로 변경
- [x] '한턴 감옥' -> '두턴 감옥'로 수정
- [x] 노드 타입 '두턴 감옥' -> 확률적으로 가게 변경
- [x] 노드 타입 "시작 노드로 복귀", "중간 숫자 지점 노드들로 이동", "슬라이드" 추가
- [x] 우승자 없이 라운드가 끝났을 때 우승자 없게 수정

**Frontend**
- [x] '두 턴 감옥'시 감옥으로 이동 
- [x] 노드 타입별로 색상 부여
- [x] 노드 타입들 설명 화면에 보여줌
- [x] 라운드 상황 및 전진 실패 조건 화면에 표시
- [x] 우승자 결과 보드 UI 구현
- [x] 경주 종료 시 우승자 알림창 및 축하 애니메이션 적용
- [x] 무한 루프 선 변경
--- 


### ✅ v3.0 서비스 운영 및 금융 시스템 도입 (구현완료)

**Backend**
- [x] 데이터 베이스 및 환경 구축
- [x] 핵심 엔티티 및 레포지토리 설계(사용자 정보, 등록 차량, 총상금)
- [x] 사용자 인증 및 인가 시스템 구축
- [x] 회원가입 기능 구현
- [x] 로그인, 로그아웃 및 세션 관리 구현 
- [x] 잔액 충전 및 조회 기능 구현
- [x] 로그인 된 사용자가 여러 대 자동차 등록 기능 구현
- [x] 등록 차량 수에 따라 잔액에서 차감 및 상금에 추가 기능 구현
- [x] 우승자 발생시, 상금 지급, 상금 초기화, 모든 자동차 정보 삭제 기능 구현
- [x] 실제 시간 5분마다 경주 자동 시작 기능 구현
- [x] 특수 노드 진입 시 발생 이벤트 메시지를 수집하여 JSON에 포함

**Frontend**
- [x] 회원가입 및 로그인, 로그아웃 폼,버튼 구현
- [x] 로그인 시 화면 상단 닉네임 표시
- [x] 잔액 충전 입력 폼 및 연동 구현
- [x] 사용자 전용 자동차 등록 폼 구현
- [x] 경주 자동차들을 데이터베이스 자동차로 변경 (본인 자동차에만 색상 부여)
- [x] 사용자가 등록한 자동차 대수 닉네임 옆 표시
- [x] 누적 총 상금 금액 표시 UI 구현
- [x] 경기가 진행하지 않을 때 라운드 영역은 경기 시작까지 남은시간 타이머로 전환
- [x] 경주 시작 10초 전 자동차 등록, 잔액 충전 폼,로그 아웃 자동 비활성화
- [x] 경주 자동 10초 전 경기화면 초기화 기능 구현
- [x] 경기 시작 3초전, 화면 중앙에"3,2,1" 애니메이션 구현
- [x] 특수 노드 이벤트 메시지를 화면의 전용 로그 영역에 실시간으로 표시.
---
