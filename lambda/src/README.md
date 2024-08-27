## 시나리오

### 1. 예매 시작

#### 사용자는 예매 페이지에 접속한다.

1. 클라이언트는 서버의 좌석 조회 API를 호출한다.
    1. 서버는 사용자의 인증 정보를 획득한다. 인증 정보를 이용하여 대기열 시스템에 사용자가 작업 가능한지 확인한다.
        1. 작업이 허용된 경우 서버는 200 OK, 좌석 목록을 반환한다. **실패**를 기록하고 시나리오를 종료한다.
        2. 작업이 허용되지 않은 경우 307 임시 리다이렉트를 응답한다. 이후 **2. 대기열 단계**로 이동한다.

### **2. 대기열 단계**

#### 사용자는 대기열 페이지에 접속한다.

1. 클라이언트는 서버의 남은 순번 조회 API를 호출한다.
    1. 서버는 사용자의 인증 정보를 획득한다. 인증 정보를 이용하여 대기열 시스템에게 사용자의 남은 순번을 확인한다.
        1. 남은 순번이 0보다 큰 경우 5초 대기한다. 이후 **2. 대기열 단계**의 처음으로 이동한다.
        2. 남은 순번이 0과 같거나 작은 경우 **3. 티켓팅 단계**로 이동한다.

### 3. 티켓팅 단계

1. 클라이언트는 서버의 좌석 조회 API를 호출한다.
    1. 서버는 사용자의 인증 정보를 획득한다. 인증 정보를 이용하여 대기열 시스템에 사용자가 작업 가능한지 확인한다.
        1. 작업이 허용된 경우 서버는 200 OK,  좌석 목록을 반환한다.
            - 좌석이 남아있는 경우 **4. 좌석 선택 단계**로 이동한다.
            - 좌석이 남아있지 않은 경우 15초 대기한다. **이후 단계가 스킵되었음**를 기록하고 시나리오를 종료한다.
        2. 작업이 허용되지 않은 경우 307 임시 리다이렉트를 응답한다. **실패**를 기록하고 시나리오를 종료한다.

### 4. 좌석 선택

1. 사용자는 좌석을 선택하고 다음 단계 버튼을 클릭한다.
2. 클라이언트는 서버의 좌석 점유 API를 호출한다.
    1. 좌석을 점유한 경우 200 OK를 반환한다. 사용자는 **5. 결제 단계**로 이동한다.
    2. 좌석을 점유하지 못한 경우 **3. 티켓팅 단계**로 이동한다.

### 5. 결제 단계

1. 사용자는 결제하기 버튼을 클릭한다.
2. 클라이언트는 서버의 결제 API를 호출한다.
    1. 서버는 3초 대기 후 결제 성공을 응답한다.
    2. 대기열 시스템은 사용자의 작업, 대기 정보를 삭제한다.
    3. **시나리오 성공**을 기록하고 종료한다.