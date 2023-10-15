package com.epsonconnectjava.http;

import okhttp3.*;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
public class RealHttpClient implements HttpClient {

    private String baseUrl;
    private String printerEmail;
    private String clientId;
    private String clientSecret;

    private LocalDateTime expiresAt;
    private String accessToken = "";
    private String refreshToken = "";
    private String subjectId = "";

    private OkHttpClient client;

    public RealHttpClient(String baseUrl , String printerEmail, String clientId, String clientSecret) {
        this.baseUrl = baseUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;

        this.expiresAt = LocalDateTime.now();
        this.client = new OkHttpClient();
        this.printerEmail =  printerEmail;
    }

    @Override
    public JSONObject send(Request.Builder requestBuilder) {
        JSONObject responseBody = null;
        Request request = requestBuilder.build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

             responseBody = new JSONObject(response.body().string());
            if (responseBody.has("error")) {
                throw new Exception(responseBody.getString("error"));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseBody;
    }


    private Map<String, String> getDefaultHeaders() {
        Map<String, String> defaultHeaders = new HashMap<>();
        defaultHeaders.put("Authorization", "Bearer " + this.accessToken);
        defaultHeaders.put("Content-Type", "application/json");
        return defaultHeaders;
    }

    public static class ApiError extends RuntimeException {
        public ApiError(String message) {
            super(message);
        }
    }
}
