package com.thirdparty.ticketing;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.dto.SettingInfo;
import com.thirdparty.ticketing.dto.request.LoginRequest;
import com.thirdparty.ticketing.dto.request.SeatSelectionRequest;
import com.thirdparty.ticketing.dto.request.TicketPaymentRequest;
import com.thirdparty.ticketing.dto.response.ItemResult;
import com.thirdparty.ticketing.dto.response.LoginResponse;
import com.thirdparty.ticketing.dto.response.RemainingCountResponse;
import com.thirdparty.ticketing.dto.response.SeatElement;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final String AUTHORIZATION = "Authorization";
    private static final String PERFORMANCE_ID = "performanceId";
    private static final ObjectMapper mapper = new ObjectMapper();

    private record Result(int firstSuccess,
                          int secondSuccess,
                          int thirdAndFourthSuccess,
                          int fifthSuccess,
                          int totalSuccess,
                          int skipped,
                          int serverFailed,
                          int lambdaFailed,
                          long totalSpendTime,
                          List<String> errors) {
    }

    private HttpClient httpClient;

    private LambdaLogger logger;

    private AtomicInteger firstSuccess = new AtomicInteger(0);
    private AtomicInteger secondSuccess = new AtomicInteger(0);
    private AtomicInteger fourthSuccess = new AtomicInteger(0);
    private AtomicInteger fifthSuccess = new AtomicInteger(0);

    private AtomicInteger successCount = new AtomicInteger(0); // 시나리오 성공. 모든 단계 진행.
    private AtomicInteger serverFailedCount = new AtomicInteger(0);  // 시나리오 실패. 비정상 종료.
    private AtomicInteger skippedCount = new AtomicInteger(0);  // 시나리오 성공. 티켓팅 단계에서 종료.
    private AtomicInteger lambdaFailedCount = new AtomicInteger(0); // 람다 예외.

    private long performanceId;

    private List<String> errors = new ArrayList<>();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent event,
            Context context) {
        mapper.setSerializationInclusion(Include.NON_NULL);
        logger = context.getLogger();
        httpClient = HttpClient.newHttpClient();

        String body = event.getBody();
        SettingInfo setting;
        try {
            setting = mapper.readValue(body, SettingInfo.class);
        } catch (JsonProcessingException e) {
            return new APIGatewayProxyResponseEvent().withStatusCode(400);
        }
        String uri = setting.uri();
        int offset = setting.offset();
        int limit = setting.limit();
        performanceId = setting.performanceId();

        Random random = new Random(System.currentTimeMillis());
        ExecutorService executorService = Executors.newFixedThreadPool(limit);

        long startTime = System.currentTimeMillis();
        try {
            process(executorService, random, offset, limit, uri);
        } catch (Exception e) {
            logger.log("람다 예외 발생.");
            lambdaFailedCount.incrementAndGet();
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500);
        }
        long endTime = System.currentTimeMillis();

        long totalSpentTime = (endTime - startTime) / 1000;
        Result result = new Result(
                firstSuccess.get(),
                secondSuccess.get(),
                fourthSuccess.get(),
                fifthSuccess.get(),
                successCount.get(),
                skippedCount.get(),
                serverFailedCount.get(),
                lambdaFailedCount.get(),
                totalSpentTime,
                errors);
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(writeValueAsString(result));
    }

    private void process(ExecutorService executorService, Random random, int offset, int limit, String uri)
            throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(limit);
        for (int i = 0; i < limit; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    int userNumber = offset + finalI + 2;

                    infoLog(0, userNumber, "로그인 시작.");
                    String accessToken = login(userNumber, uri);
                    String authorizationHeader = "Bearer " + accessToken;
                    infoLog(0, userNumber, "로그인 성공.");

                    boolean proceed = true;
                    // 1. 예매 시작 단계
                    proceed = firstStory(uri, userNumber, authorizationHeader);

                    // 2. 대기열 단계
                    if (proceed) {
                        proceed = secondStory(uri, userNumber, authorizationHeader);
                    }

                    // 3. 티켓팅 단계 && 4. 좌석 선택 단계
                    SeatElement seat = null;
                    if (proceed) {
                        while (true) {
                            // 3. 티켓팅 단계
                            infoLog(3, userNumber, "티켓팅 단계 시작.");
                            HttpResponse<String> getSeats = getPerformanceSeats(uri, authorizationHeader);
                            int statusCode = getSeats.statusCode();
                            if (statusCode != 200) {
                                serverFailedCount.incrementAndGet();
                                if (getSeats.body() != null) {
                                    errors.add(getSeats.body());
                                }
                                infoLog(3, userNumber, "시나리오 실패. 상태 코드=" + statusCode);
                                seat = null;
                                break;
                            }

                            ItemResult<SeatElement> seats = readValues(getSeats.body());
                            List<SeatElement> list = seats.items().stream()
                                    .filter(SeatElement::seatAvailable)
                                    .toList();

                            if (list.isEmpty()) {
                                infoLog(3, userNumber, "남은 좌석 없음. 30초 대기");
                                Thread.sleep(15000);
                                skippedCount.incrementAndGet();
                                infoLog(3, userNumber, "남은 좌석 없음. 시나리오 종료.");
                                leaveWaiting(uri, authorizationHeader);
                                seat = null;
                                break;
                            }
                            infoLog(3, userNumber, "티켓팅 단계 종료. 좌석 선택 단계 이동.");

                            // 4. 좌석 선택 단계
                            infoLog(4, userNumber, "좌석 선택 단계 시작. 500 밀리초 대기.");
                            sleep(500);

                            seat = list.get(random.nextInt(list.size()));
                            HttpResponse<String> selectSeatResponse = selectSeat(uri, seat,
                                    authorizationHeader);
                            if (selectSeatResponse.statusCode() != 200) {
                                seat = null;
                                infoLog(4, userNumber, "좌석 선택 실패. 티켓팅 단계 이동.");
                                continue;
                            }

                            fourthSuccess.incrementAndGet();
                            infoLog(4, userNumber, "티켓팅 단계 종료. 결제 단계 이동.");
                            break;
                        }
                    }

                    // 5. 결제 단계
                    if (seat != null) {
                        fifthStory(uri, userNumber, seat, authorizationHeader);
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    lambdaFailedCount.incrementAndGet();
                    logger.log("람다 예외 발생.", LogLevel.WARN);
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();
    }

    private String login(int userNumber, String uri) {
        String email = "member-" + userNumber + "@example.com";
        String password = "password";
        String messageBody = writeValueAsString(new LoginRequest(
                email,
                password
        ));

        HttpRequest apiLoginRequest = HttpRequest
                .newBuilder(URI.create(uri + "/api/login"))
                .POST(BodyPublishers.ofString(messageBody))
                .header("Content-Type", "application/json")
                .build();

        LoginResponse loginResponse = sendRequest(apiLoginRequest, LoginResponse.class);
        return loginResponse.accessToken();
    }

    private boolean firstStory(String uri, int userNumber, String authorizationHeader) {
        infoLog(1, userNumber, "예매 시작 단계 시작.");
        HttpResponse<String> getSeats = getPerformanceSeats(uri, authorizationHeader);  // 1) 좌석 조회
        int statusCode = getSeats.statusCode();
        if (statusCode == 307) {
            firstSuccess.incrementAndGet();
            infoLog(1, userNumber, "예매 시작 단계 종료. 대기열 단계 이동.");
            return true;
        }

        if (statusCode == 200) {
            infoLog(1, userNumber, "200 나오면 안됨. 대기열 시스템 오류.");
        }
        if (statusCode != 200 && getSeats.body() != null) {
            errors.add(getSeats.body());
        }

        serverFailedCount.incrementAndGet();
        infoLog(1, userNumber, "시나리오 실패. 상태 코드=" + statusCode);
        return false;

    }

    private boolean secondStory(String uri, int userNumber, String authorizationHeader) {
        infoLog(2, userNumber, "대기열 단계 시작.");
        while (true) {
            HttpResponse<String> response = getRemainingCount(uri, authorizationHeader);
            if (response.statusCode() != 200) {
                serverFailedCount.incrementAndGet();
                infoLog(2, userNumber, "시나리오 실패. 상태 코드=" + response.statusCode());
                if (response.body() != null) {
                    errors.add(response.body());
                }
                return false;
            }
            RemainingCountResponse remainingCountResponse = readValue(response.body(), RemainingCountResponse.class);
            int remainingCount = remainingCountResponse.remainingCount();
            infoLog(2, userNumber, "남은 순번=" + remainingCount);
            if (remainingCount <= 0) {
                break;
            }
            infoLog(2, userNumber, "5초 대기 후 재요청.");
            sleep(10000);
        }
        secondSuccess.incrementAndGet();
        infoLog(2, userNumber, "대기열 단계 종료. 티켓팅 단계 이동.");
        return true;
    }

    private void fifthStory(String uri, int userNumber, SeatElement seat, String authorizationHeader) {
        infoLog(5, userNumber, "결제 단계 시작.");
        TicketPaymentRequest ticketPaymentRequest = new TicketPaymentRequest(seat.seatId());
        String value = writeValueAsString(ticketPaymentRequest);
        HttpRequest apiReservationTicket = HttpRequest
                .newBuilder(URI.create(uri + "/api/tickets"))
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION, authorizationHeader)
                .header(PERFORMANCE_ID, String.valueOf(performanceId))
                .POST(BodyPublishers.ofString(value))
                .build();
        HttpResponse<String> reservationTicketResponse = sendRequest(apiReservationTicket);
        if (reservationTicketResponse.statusCode() != 200) {
            infoLog(5, userNumber, "시나리오 실패. 상태 코드=" + reservationTicketResponse.statusCode());
            serverFailedCount.incrementAndGet();

            if (reservationTicketResponse.body() != null) {
                errors.add(reservationTicketResponse.body());
            }
            return;
        }

        infoLog(5, userNumber, "결제 단계 종료.");
        fifthSuccess.incrementAndGet();
    }

    private ItemResult<SeatElement> readValues(String body) {
        try {
            return mapper.readValue(body, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpResponse<String> getPerformanceSeats(String uri, String authorizationHeader) {
        HttpRequest apiTicketingRequest = HttpRequest
                .newBuilder(URI.create(uri + "/api/performances/" + performanceId + "/seats"))
                .GET()
                .header(AUTHORIZATION, authorizationHeader)
                .header(PERFORMANCE_ID, String.valueOf(performanceId))
                .build();
        return sendRequest(apiTicketingRequest);
    }

    private HttpResponse<String> getRemainingCount(String uri, String authorizationHeader) {
        HttpRequest apiRemainingCountRequest = HttpRequest
                .newBuilder(URI.create(uri + "/api/performances/" + performanceId + "/wait"))
                .header(AUTHORIZATION, authorizationHeader)
                .header(PERFORMANCE_ID, String.valueOf(performanceId))
                .GET()
                .build();
        return sendRequest(apiRemainingCountRequest);
    }

    private void leaveWaiting(String uri, String authorizationHeader) {
        HttpRequest leaveWaitingRequest = HttpRequest
                .newBuilder(URI.create(uri + "/performances/" + performanceId + "/wait"))
                .DELETE()
                .header(AUTHORIZATION, authorizationHeader)
                .header(PERFORMANCE_ID, String.valueOf(performanceId))
                .build();
        sendRequest(leaveWaitingRequest);
    }

    private HttpResponse<String> selectSeat(String uri, SeatElement seatElement, String authorizationHeader) {
        SeatSelectionRequest request = new SeatSelectionRequest(seatElement.seatId());
        String value = writeValueAsString(request);
        HttpRequest apiSelectSeatRequest = HttpRequest
                .newBuilder(URI.create(uri + "/api/seats/select"))
                .POST(BodyPublishers.ofString(value))
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION, authorizationHeader)
                .header(PERFORMANCE_ID, String.valueOf(performanceId))
                .build();
        return sendRequest(apiSelectSeatRequest);
    }

    private void sleep(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            return httpClient.send(request, BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T sendRequest(HttpRequest request, Class<T> clazz) {
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return readValue(response.body(), clazz);
    }

    private String writeValueAsString(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T readValue(String value, Class<T> valueType) {
        try {
            return mapper.readValue(value, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void infoLog(int storyNumber, int userNumber, String message) {
        logger.log(storyNumber + ". 사용자-" + userNumber + " " + message);
    }
}
