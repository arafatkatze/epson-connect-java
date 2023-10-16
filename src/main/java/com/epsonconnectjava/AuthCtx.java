package com.epsonconnectjava;

import com.epsonconnectjava.http.HttpClient;
import com.epsonconnectjava.http.RealHttpClient;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * AuthCtx class is responsible for handling authentication with the Epson Connect API.
 * It also provides utility methods to send HTTP requests using a specified HttpClient.
 */
public class AuthCtx {

    private static final Logger logger = Logger.getLogger(AuthCtx.class.getName());
    // HTTP client used for making API calls
    private final HttpClient httpClient;
    // API base URL
    public String baseUrl;
    // Access token for API authentication
    public String accessToken;
    // Email associated with the printer
    private String printerEmail;
    // API client ID for authentication
    private String clientId;
    // API client secret for authentication
    private String clientSecret;
    // Date when the access token expires
    private Date expiresAt;
    // Refresh token for renewing the access token
    private String refreshToken;
    // Subject ID (typically representing the device ID)
    private String subjectId;

    /**
     * Constructor for AuthCtx with specified parameters.
     *
     * @param baseUrl Base URL for the API.
     * @param printerEmail Email associated with the printer.
     * @param clientId Client ID for API authentication.
     * @param clientSecret Client secret for API authentication.
     */
    public AuthCtx(String baseUrl, String printerEmail, String clientId, String clientSecret) {
        this(new RealHttpClient(baseUrl, printerEmail, clientId, clientSecret), baseUrl, printerEmail, clientId, clientSecret);
    }

    /**
     * Main constructor for AuthCtx.
     *
     * @param httpClient The HTTP client to use for making requests.
     * @param baseUrl Base URL for the API.
     * @param printerEmail Email associated with the printer.
     * @param clientId Client ID for API authentication.
     * @param clientSecret Client secret for API authentication.
     */
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
        auth();
    }

    /**
     * Sends an HTTP request using the specified request builder.
     *
     * @param requestBuilder The request builder to use for creating the HTTP request.
     * @return A JSONObject containing the response data.
     * @throws IOException If there's an error during the HTTP request.
     */
    public JSONObject send(Request.Builder requestBuilder) throws IOException {
        return this.httpClient.send(requestBuilder);
    }

    /**
     * Authenticates the client with the Epson Connect API.
     * If the client is already authenticated and the token hasn't expired, it does nothing.
     * Otherwise, it refreshes the token or authenticates for the first time.
     */
    public void auth() {
        if (this.expiresAt.after(new Date())) {
            return;
        }

        Map<String, String> data = new HashMap<>();
        if (this.accessToken.isEmpty()) {
            data.put("grant_type", "password");
            data.put("username", this.printerEmail);
            data.put("password", "");
        } else {
            data.put("grant_type", "refresh_token");
            data.put("refresh_token", this.refreshToken);
        }

        try {
            String credentials = Credentials.basic(this.clientId, this.clientSecret);
            RequestBody formBody = new FormBody.Builder()
                    .add("grant_type", "password")
                    .add("username", this.printerEmail)
                    .add("password", "")
                    .build();
            Request.Builder requestBuilder = new Request.Builder()
                    .url(baseUrl + "/api/1/printing/oauth2/auth/token?subject=printer")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", credentials)
                    .post(formBody);

            JSONObject body = httpClient.send(requestBuilder);

            if (body.has("error")) {
                throw new AuthenticationError(body.getString("error"));
            }

            if (this.accessToken.isEmpty()) {
                this.refreshToken = body.getString("refresh_token");
            }

            this.expiresAt = new Date(System.currentTimeMillis() + body.getLong("expires_in") * 1000);
            this.accessToken = body.getString("access_token");
            this.subjectId = body.getString("subject_id");
        } catch (IOException e) {
            logger.severe("IO Error: " + e.getMessage());
        } catch (ApiError e) {
            logger.severe("API Error: " + e.getMessage());
        } catch (AuthenticationError e) {
            logger.severe("Authentication Error: " + e.getMessage());
        }
    }


    /**
     * Deauthenticates the client from the Epson Connect API.
     *
     * @throws IOException If there's an error during deauthentication.
     */
    public void deauthenticate() throws IOException {
        Request.Builder requestBuilder = new Request.Builder()
                .url(this.baseUrl + "/api/1/printing/printers/" + this.subjectId)
                .delete();
        httpClient.send(requestBuilder);
    }


    public String getDeviceId() {
        return this.subjectId;
    }

    // Getter for accessToken
    public String getAccessToken() {
        return accessToken;
    }

    // Setter for accessToken
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    // Getter for refreshToken
    public String getRefreshToken() {
        return refreshToken;
    }

    // Setter for refreshToken
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // Getter for expiresAt
    public Date getExpiresAt() {
        return expiresAt;
    }

    // Setter for expiresAt
    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    /**
     * Exception indicating an authentication error with the API.
     */
    public static class AuthenticationError extends RuntimeException {
        public AuthenticationError(String message) {
            super(message);
        }
    }

    /**
     * Exception indicating a general API error.
     */
    public static class ApiError extends RuntimeException {
        public ApiError(String message) {
            super(message);
        }
    }
}
