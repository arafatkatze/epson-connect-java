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
    public JSONObject send(String method, String path, Map<String, String> data, Map<String, String> headers) {
        if (headers == null) {
            headers = getDefaultHeaders();
        }
        JSONObject responseBody = null;
        RequestBody formBody = null;

//        if ("POST".equalsIgnoreCase(method)) {
            formBody = new FormBody.Builder()
                    .add("grant_type", "password")
                    .add("username", this.printerEmail)
                    .add("password", "")
                    .build();
//        }

        String credentials = Credentials.basic(this.clientId, this.clientSecret);
        Request.Builder requestBuilder = new Request.Builder()
                .url(baseUrl + path)
                .header("Authorization", credentials);


        if ("POST".equalsIgnoreCase(method) && formBody != null) {
            requestBuilder.header("Content-Type", "application/x-www-form-urlencoded");
            requestBuilder.post(formBody);
        } else if ("GET".equalsIgnoreCase(method)) {
            requestBuilder.header("Content-Type", "application/json");
            requestBuilder.header("Authorization", "Bearer " + headers.get("access_token"));
            requestBuilder.get();
        } else {
            throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        Request request = requestBuilder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

             responseBody = new JSONObject(response.body().string());
            if (responseBody.has("error")) {
                throw new Exception(responseBody.getString("error"));
            }

            if (accessToken.isEmpty()) {
                refreshToken = responseBody.getString("refresh_token");
            }

            expiresAt = LocalDateTime.now().plusSeconds(responseBody.getLong("expires_in"));
            accessToken = responseBody.getString("access_token");
            subjectId = responseBody.getString("subject_id");

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
