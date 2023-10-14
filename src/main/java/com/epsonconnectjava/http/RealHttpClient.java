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

    public RealHttpClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.clientId = "a243e42e187e469f8e9c6e2383b7e2e6";
        this.clientSecret = "PDLDVwcHI7eX4oL2jHGEdIgl0EK9iMdjNkXumi2tZIgaeyG5AKtGqgHQCEyNZGsR";

        this.expiresAt = LocalDateTime.now();
        this.client = new OkHttpClient();
        this.printerEmail = "pdx3882hvp0q97@print.epsonconnect.com";
    }

    @Override
    public JSONObject send(String method, String path, Map<String, String> data, Map<String, String> headers) {
        if (headers == null) {
            headers = getDefaultHeaders();
        }
        JSONObject responseBody = null;
        RequestBody formBody;
        formBody = new FormBody.Builder()
                .add("grant_type", "password")
                .add("username", this.printerEmail)
                .add("password", "")
                .build();
        String credentials = Credentials.basic(this.clientId, this.clientSecret);
        Request request = new Request.Builder()
                .url(baseUrl + path)
                .post(formBody)
                .header("Authorization", credentials)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

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
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer ..."); // Modify as needed
        headers.put("Content-Type", "application/json");
        return headers;
    }

    public static class ApiError extends RuntimeException {
        public ApiError(String message) {
            super(message);
        }
    }
}
