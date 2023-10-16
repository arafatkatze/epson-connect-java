package com.epsonconnectjava.http;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;

/**
 * RealHttpClient class that provides functionality to send HTTP requests.
 * This class implements the HttpClient interface.
 */
public class RealHttpClient implements HttpClient {

    // Base URL for the API endpoint
    private String baseUrl;

    // HTTP client from OkHttp library
    private OkHttpClient client;

    /**
     * Constructor for RealHttpClient
     *
     * @param baseUrl      The base URL for the API
     * @param printerEmail Email for the printer (unused in the provided code but retained as a parameter)
     * @param clientId     Client ID for authentication (unused in the provided code but retained as a parameter)
     * @param clientSecret Client secret for authentication (unused in the provided code but retained as a parameter)
     */
    public RealHttpClient(String baseUrl, String printerEmail, String clientId, String clientSecret) {
        this.baseUrl = baseUrl;
        this.client = new OkHttpClient();
    }

    /**
     * Send an HTTP request.
     *
     * @param requestBuilder Request builder containing the details of the HTTP request.
     * @return JSONObject containing the response from the server.
     */
    @Override
    public JSONObject send(Request.Builder requestBuilder) throws IOException {
        JSONObject responseBody = null;
        Request request = requestBuilder.build();
        try (Response response = client.newCall(request).execute()) {

            // Check if the response is successful, if not, throw an exception
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseBodyStr = response.body().string();

            if (responseBodyStr.trim().isEmpty()) {
                return null;
            }
            // Parse the response body to a JSON object
            responseBody = new JSONObject(responseBodyStr);

            // If the response contains an error, throw an exception
            if (responseBody.has("error")) {
                throw new ApiError(responseBody.getString("error"));
            }
        } catch (IOException e) {
            throw e;
        }
        return responseBody;
    }

    /**
     * Custom exception class for API errors.
     */
    public static class ApiError extends RuntimeException {
        public ApiError(String message) {
            super(message);
        }
    }
}
