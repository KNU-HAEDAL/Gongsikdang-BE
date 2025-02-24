package com.food.service;

import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {
    private static final String PORTONE_API_URL = "https://api.iamport.kr";
    private static final String PORTONE_API_KEY = "5682867700354005";
    private static final String PORTONE_API_SECRET = "VJL6GqWeXu3LWx2d37uFkdXQPeWSnAQjutjDKjkuXysM9gCk1JQXkk1rz3owzstHy0mVYtzlJw4jquAO";

    /**
     * 포트원 Access Token 발급
     */
    private String getAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        String url = PORTONE_API_URL + "/users/getToken";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("imp_key", PORTONE_API_KEY);
        requestBody.put("imp_secret", PORTONE_API_SECRET);

        String response = restTemplate.postForObject(url, requestBody, String.class);
        JSONObject jsonResponse = new JSONObject(response);
        return jsonResponse.getJSONObject("response").getString("access_token");
    }

    public String getImpUidByMerchantUid(String merchantUid) {
        try {
            String accessToken = getAccessToken();
            RestTemplate restTemplate = new RestTemplate();
            String url = PORTONE_API_URL + "/payments/find/" + merchantUid;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JSONObject jsonResponse = new JSONObject(response.getBody());
            String impUid = jsonResponse.getJSONObject("response").getString("imp_uid");

            return impUid;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get imp_uid from Portone");
        }
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

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JSONObject jsonResponse = new JSONObject(response.getBody());
            int amount = jsonResponse.getJSONObject("response").getInt("amount");
            String status = jsonResponse.getJSONObject("response").getString("status");

            // 🔥 금액과 상태("paid") 검증
            return (amount == expectedAmount) && "paid".equals(status);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 결제 취소 API (포트원 REST API)
     */
    public boolean cancelPayment(String impUid, String reason) {
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
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            JSONObject jsonResponse = new JSONObject(response.getBody());
            return jsonResponse.getInt("code") == 0; // 성공 여부 확인
        } catch (Exception e) {
            return false;
        }
    }
}
