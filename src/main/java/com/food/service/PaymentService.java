package com.food.service;

import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {
    private static final String PORTONE_API_URL = "https://api.iamport.kr";
    private static final String PORTONE_API_KEY = "5682867700354005";
    private static final String PORTONE_API_SECRET = "VJL6GqWeXu3LWx2d37uFkdXQPeWSnAQjutjDKjkuXysM9gCk1JQXkk1rz3owzstHy0mVYtzlJw4jquAO";

    private String cachedAccessToken;
    private Instant tokenExpiryTime;

    /**
     * 포트원 Access Token 발급 (30분간 유지)
     */
    private String getAccessToken() {
        if (cachedAccessToken != null && Instant.now().isBefore(tokenExpiryTime)) {
            return cachedAccessToken; // 캐싱된 토큰 반환
        }

        RestTemplate restTemplate = new RestTemplate();
        String url = PORTONE_API_URL + "/users/getToken";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("imp_key", PORTONE_API_KEY);
        requestBody.put("imp_secret", PORTONE_API_SECRET);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestBody, String.class);
        JSONObject jsonResponse = new JSONObject(response.getBody());

        cachedAccessToken = jsonResponse.getJSONObject("response").getString("access_token");
        tokenExpiryTime = Instant.now().plusSeconds(1800); // 30분 후 만료

        return cachedAccessToken;
    }

    /**
     * 결제 검증 API (포트원 REST API)
     */
    public boolean verifyPayment(String impUid, int expectedAmount) {
        try {
            String accessToken = getAccessToken();
            RestTemplate restTemplate = new RestTemplate();
            String url = PORTONE_API_URL + "/payments/" + impUid;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            System.out.println("✅ [verifyPayment] 요청 URL: " + url);
            System.out.println("✅ [verifyPayment] impUid: " + impUid);
            System.out.println("✅ [verifyPayment] expectedAmount: " + expectedAmount);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JSONObject jsonResponse = new JSONObject(response.getBody());

            System.out.println("✅ [verifyPayment] 포트원 응답: " + jsonResponse.toString());

            if (!jsonResponse.has("response")) {
                System.out.println("결제 정보를 찾을 수 없음");
                throw new RuntimeException("결제 정보를 찾을 수 없음.");
            }

            int amount = jsonResponse.getJSONObject("response").getInt("amount");
            String status = jsonResponse.getJSONObject("response").getString("status");

            System.out.println("✅ [verifyPayment] 결제된 금액: " + amount);
            System.out.println("✅ [verifyPayment] 결제 상태: " + status);

            // 🔥 금액과 상태 검증
            if (amount != expectedAmount || !"paid".equals(status)) {
                System.out.println("결제 검증 실패로 인한 자동 환불");
                cancelPayment(impUid, "결제 검증 실패로 인한 자동 환불");
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 결제 정보 조회 API
     */
    public Integer getPaymentAmount(String impUid) {
        try {
            String accessToken = getAccessToken();
            String url = PORTONE_API_URL + "/payments/" + impUid;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();

            System.out.println("🔍 [getPaymentAmount] 결제 조회 요청: " + url);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            System.out.println("✅ [getPaymentAmount] 응답 코드: " + response.getStatusCode());
            System.out.println("✅ [getPaymentAmount] 응답 데이터: " + response.getBody());

            JSONObject jsonResponse = new JSONObject(response.getBody());
            if (jsonResponse.getInt("code") != 0) {
                System.out.println("❌ [getPaymentAmount] 결제 정보 조회 실패! 응답 코드: " + jsonResponse.getInt("code"));
                return null;
            }

            // ✅ 결제 금액 가져오기
            Integer amount = jsonResponse.getJSONObject("response").getInt("amount");
            System.out.println("✅ [getPaymentAmount] 결제 금액 조회 성공: " + amount);
            return amount;

        } catch (Exception e) {
            System.out.println("❌ [getPaymentAmount] 예외 발생: " + e.getMessage());
            return null;
        }
    }




    /**
     * 결제 취소 API
     */
    public boolean cancelPayment(String impUid, String reason) {
        if (reason == null || reason.isBlank()) {
            reason = "결제 후 미사용 1시간이 지나 환불"; // 기본 사유 설정
        }

        try {
            String accessToken = getAccessToken();
            RestTemplate restTemplate = new RestTemplate();
            String url = PORTONE_API_URL + "/payments/cancel";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("imp_uid", impUid);
            requestBody.put("reason", reason);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            System.out.println("🔍 [cancelPayment] 결제 취소 요청: " + url);
            System.out.println("🔍 [cancelPayment] 요청 데이터: " + requestBody);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            System.out.println("✅ [cancelPayment] 응답 코드: " + response.getStatusCode());
            System.out.println("✅ [cancelPayment] 응답 데이터: " + response.getBody());

            JSONObject jsonResponse = new JSONObject(response.getBody());
            boolean success = jsonResponse.getInt("code") == 0;

            if (success) {
                System.out.println("✅ [cancelPayment] 결제 취소 성공!");
            } else {
                System.out.println("❌ [cancelPayment] 결제 취소 실패! 응답 코드: " + jsonResponse.getInt("code"));
            }

            return success;
        } catch (Exception e) {
            System.out.println("❌ [cancelPayment] 예외 발생: " + e.getMessage());
            return false;
        }
    }

}
