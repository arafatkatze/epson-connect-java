package com.epsonconnectjava.http;

import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.HashMap;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import org.json.JSONObject;
import java.util.logging.Logger;

public class RealHttpClient implements HttpClient {

    private final String baseUrl;
    private final Logger logger = Logger.getLogger(RealHttpClient.class.getName());

    public RealHttpClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public JSONObject send(String method, String path, Map<String, String> data, Map<String, String> headers) {
        if (headers == null) {
            headers = getDefaultHeaders();
        }

        logger.info(String.format("%s %s data=%s headers=%s", method, path, data, headers));

        try {
            URL url = new URL(baseUrl + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);

            for (Map.Entry<String, String> header : headers.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }

            if (data != null) {
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                os.write(data.toString().getBytes());
                os.flush();
                os.close();
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());

            String error = jsonResponse.optString("code");
            if (!error.isEmpty()) {
                throw new ApiError(error);
            }

            return jsonResponse;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
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
