# 우아한 티켓팅

## 프로젝트 소개 

리소스가 한정된 상황에서 대량의 트래픽을 견딜 수 있는 안정적인 티켓팅 서비스입니다.

## 프로젝트 목표

### 핵심 목표

- 좌석 선점이 동시에 이루어져도 안전한 티켓팅 시스템을 설계
- 새로고침해도 순서가 유지되는 친절한 대기열을 시스템을 설계

### 부가 목표

- 사용자 경험을 향상시키기 위해 실시간 좌석 선점 상황을 클라이언트에 반영

## 👨‍👩‍👧‍👦 팀원 소개

<table>
    <tr align="center">
        <td><B>Backend</B></td>
        <td><B>Backend</B></td>
        <td><B>Backend</B></td>
        <td><B>Backend</B></td>
    </tr>
    <tr align="center">
        <td><a href="https://github.com/hseong3243">박혜성</a></td>
        <td><a href="https://github.com/seminchoi">최세민</a></td>
        <td><a href="https://github.com/mirageoasis">김현우</a></td>
        <td><a href="https://github.com/lass9436">이영민</a></td>
    </tr>
    <tr align="center">
        <td>
            <img src="https://github.com/hseong3243.png?size=130">
        </td>
        <td>
            <img src="https://github.com/seminchoi.png?size=100">
        </td>
        <td>
            <img src="https://github.com/mirageoasis.png" width="100">
        </td>
        <td>
            <img src="https://github.com/lass9436.png?size=100">
        </td>
    </tr>
</table>

## 사용 기술

<div> 
  <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
  <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
  <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
  <img src="https://img.shields.io/badge/redis-FF4438?style=for-the-badge&logo=redis&logoColor=white">
  <br/>

  <img src="https://img.shields.io/badge/grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white">
  <img src="https://img.shields.io/badge/prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white">
  <img src="https://img.shields.io/badge/locust-3ECC5F?style=for-the-badge&logo=locust&logoColor=white">
  <br/>

  <img src="https://img.shields.io/badge/aws-232F3E?style=for-the-badge&logo=amazonwebservices&logoColor=white">
  <img src="https://img.shields.io/badge/ec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white">
  <img src="https://img.shields.io/badge/rds-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white">
  <img src="https://img.shields.io/badge/alb-8C4FFF?style=for-the-badge&logo=awselasticloadbalancing&logoColor=white">
  <br/>
</div>

## 인프라 구성

![인프라 구성 excalidraw](https://github.com/user-attachments/assets/06d872ef-eabe-4747-87a8-063ff0fa0e88)

## 해결한 문제들

### 좌석 선점 동시성 제어를 위한 락 방식 비교 및 선정

### 짝 프로그래밍을 통한 대기열 시스템 설계

![대기열 시스템 구성](https://github.com/user-attachments/assets/4a9b35a2-1c39-47d2-af63-a23b3e649a21)

![대기열시스템_문제1](https://github.com/user-attachments/assets/dc04c956-c1da-4ff0-bd01-9c63f0a4b873)

![대기열 시스템 문제2](https://github.com/user-attachments/assets/3d78c90a-01f3-45f8-89b5-b9443a8362b9)


### 10,000명을 견딜수 있는 대기열 시스템 검증

- 다음 테스트 구성에 따라 부하 테스트를 진행
- 가상 사용자 2,500명일 때, 대부분의 요청을 1초 안에 처리함을 파악
- 최종적으로 가상 사용자 10,000명, `남은 순번 조회` 폴링 주기 5초일 때, 대기열 시스템이 안정적으로 동작할 수 있음을 검증 

<img width="1132" alt="스크린샷 2024-08-31 오후 10 09 15" src="https://github.com/user-attachments/assets/45cd32c0-4f4c-4730-83be-11ddbaa1a4e9">

<img width="1207" alt="스크린샷 2024-08-31 오후 10 18 01" src="https://github.com/user-attachments/assets/815c077e-ab7b-4dde-bc64-0645e02b0b56">

![최종 검증](https://github.com/user-attachments/assets/493831f5-01b4-49be-930d-33887efe0204)


### 부하 테스트에 기반한 대기열 페이지 응답 시간 개선

- Locust RPS, 응답시간 지표에서 그래프 이상을 인지
- Grafana 모니터링에서 Heap 메모리의 사용과 이상 지점이 유사함을 파악 
- GC 빈도를 낮추기 위해 로직을 개선
- 가상 사용자 2,500명, 테스트 시간 15분, 시나리오 `남은 순서 조회 API`를 1초 주기로 폴링하여 검증

![응답 시간 개선 excalidraw](https://github.com/user-attachments/assets/71cf70a0-622e-4177-9a89-6309e503f053)

<img width="1448" alt="응답 시간 개선2" src="https://github.com/user-attachments/assets/50314777-bc30-4410-91d3-83f2e314dfd0">


## 데모

### 대기열 화면
<img src="https://github.com/user-attachments/assets/7420a4f7-e5f3-40f6-ad5d-02c14739b4e7" width="444" height="507"/>

### 좌석 선택 화면
<img src="https://github.com/user-attachments/assets/87a1ed80-2a65-4836-a0be-f75dc9fccdcf" width="444" height="507"/>

### 전체 시나리오
https://github.com/user-attachments/assets/eb29c948-4a1c-41fd-a7b5-b6f4d167969c

## ERD
![스키마](https://github.com/user-attachments/assets/cc43dfd9-3135-47a4-b584-5e8184f1024d)

## 📜 그라운드 룰

- 스크럼과 회고는 10분 이내로 한다.
- 화요일에는 야근데이! 9시까지 코딩합니다.
- 일정이 있으면 미리 공유한다.
- 평일 10시 ~ 22시 이전까지 온콜타임(코어타임)으로 정한다.
- 온콜타임 프로젝트 관련 소통은 **Discord** 에서 진행한다.
    - 용건을 한 번에 육하원칙에 맞게 전송한다.
    - DM 을 사용하지 않고 모든 이슈를 public 하게 공유한다.
- 질문을 많이, 자유롭게 한다.
- 해당 주에 완료하지 못한 개발은 주말에 마무리한다. 그 외 주말 시간은 자유롭게 사용한다.
- 비판은 하되, 감정이 상할 수 있는 비난은 하지 않는다.

## 🚷 개발 컨벤션

https://github.com/woowa-techcamp-2024/Team3-rdParty/wiki/%EC%BB%A8%EB%B2%A4%EC%85%98
