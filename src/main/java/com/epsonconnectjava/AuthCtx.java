package com.epsonconnectjava;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.Date;

import okhttp3.*;
import org.json.JSONObject;
import com.epsonconnectjava.http.HttpClient;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import java.util.Scanner;

import com.epsonconnectjava.http.HttpClient;
import com.epsonconnectjava.http.RealHttpClient;
public class AuthCtx {
    private static final Logger logger = Logger.getLogger(AuthCtx.class.getName());
    
    private String baseUrl;
    private String printerEmail;
    private String clientId;
    private String clientSecret;
    private Date expiresAt;
    private String accessToken;
    private String refreshToken;
    private String subjectId;
    private final HttpClient httpClient;

    // Constructor with default HttpClient (RealHttpClient)
    public AuthCtx(String baseUrl, String printerEmail, String clientId, String clientSecret) {
        this(new RealHttpClient( baseUrl, printerEmail, clientId, clientSecret), baseUrl, printerEmail, clientId, clientSecret); // Use 'this' to call the other constructor with RealHttpClient as default
    }

    public AuthCtx(HttpClient httpClient, String baseUrl, String printerEmail, String clientId, String clientSecret) {
        this.baseUrl = baseUrl;
        this.printerEmail = printerEmail;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.expiresAt = new Date();
        this.accessToken = "";
        this.refreshToken = "";
        this.subjectId = "";
        this.httpClient = httpClient;
    }

    public JSONObject send(String method, String path, Map<String, String> data, Map<String, String> headers) {
        return this.httpClient.send(method,path,data,headers);
    }

    public JSONObject sendGet(String method, String path, Map<String, String> data, Map<String, String> headers) {
        headers.put("access_token", this.accessToken);
        return this.httpClient.send(method,path,data,headers);
    }

    private String mapToFormData(Map<String, String> data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (sb.length() > 0) {
                sb.append('&');
            }
            sb.append(entry.getKey()).append('=').append(entry.getValue());
        }
        return sb.toString();
    }

    public void auth() {
        String method = "POST";
        String path = "/api/1/printing/oauth2/auth/token?subject=printer";

        if (expiresAt.compareTo(new Date()) > 0) {
            return;
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        
        Map<String, String> data = new HashMap<>();
        if (accessToken.isEmpty()) {
            data.put("grant_type", "password");
            data.put("username", printerEmail);
            data.put("password", "");
        } else {
            data.put("grant_type", "refresh_token");
            data.put("refresh_token", refreshToken);
        }

        try {

            JSONObject body = httpClient.send(method, path, data, headers);
            
            String error = body.optString("error");
            if (!error.isEmpty()) {
                throw new AuthenticationError(error);
            }

            if (accessToken.isEmpty()) {
                refreshToken = body.getString("refresh_token");
            }

            long expiresIn = body.getLong("expires_in");
            expiresAt = new Date(System.currentTimeMillis() + expiresIn * 1000);
            accessToken = body.getString("access_token");
            subjectId = body.getString("subject_id");
        } catch (ApiError e) {
            throw new AuthenticationError(e.getMessage());
        }
    }

    public void deauthenticate() {
        String method = "DELETE";
        String path = "/api/1/printing/printers/" + subjectId;
        httpClient.send(method, path, null, null);
    }

    public String getDeviceId() {
        return subjectId;
    }

    public static class AuthenticationError extends RuntimeException {
        public AuthenticationError(String message) {
            super(message);
        }
    }

    public static class ApiError extends RuntimeException {
        public ApiError(String message) {
            super(message);
        }
    }
}
