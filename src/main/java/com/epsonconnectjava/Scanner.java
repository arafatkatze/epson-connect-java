package com.epsonconnectjava;

import com.epsonconnectjava.AuthCtx;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import okhttp3.*;
import org.json.JSONObject;

// Assuming the AuthCtx class is present in the same package
// import <your_package_name>.AuthCtx;

public class Scanner {
    // Define valid destination types for the scanner
    private static final Set<String> VALID_DESTINATION_TYPES;
    static {
        VALID_DESTINATION_TYPES = new HashSet<>();
        VALID_DESTINATION_TYPES.add("mail");
        VALID_DESTINATION_TYPES.add("url");
    }

    private AuthCtx authCtx;
    private String path;
    private Map<String, Map<String, String>> destinationCache = new HashMap<>();

    public Scanner(AuthCtx authCtx) {
        this.authCtx = authCtx;
        this.authCtx.auth();
        this.path = "/api/1/scanning/scanners/" + authCtx.getDeviceId() + "/destinations";
    }

    public Map<String, String> list() {
      String method = "GET";
      Map<String, String> headers = new HashMap<>();
      headers.put("Content-Type", "application/json");


        Request.Builder requestBuilder = new Request.Builder()
                .url(this.authCtx.baseUrl + path);
            requestBuilder.header("Content-Type", "application/json");
            requestBuilder.header("Authorization", "Bearer " + this.authCtx.accessToken);
            requestBuilder.get();
        JSONObject response = this.authCtx.send(requestBuilder);
      return jsonObjectToMap(response);
  }
  
  private Map<String, String> jsonObjectToMap(JSONObject jsonObject) {
    Map<String, String> map = new HashMap<>();
    for (String key : jsonObject.keySet()) {
        Object value = jsonObject.get(key);
        if (value instanceof String) {
            map.put(key, (String) value);
        } else {
            map.put(key, value.toString());
        }
    }
    return map;
}

    public Map<String, String> add() {
        String method = "GET";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        // Construct the data as a JSON string
        JSONObject data = new JSONObject();
        data.put("alias_name", "carsd");
        data.put("type", "mail");
        data.put("destination", "weqw@gmail.com");

        Request.Builder requestBuilder = new Request.Builder()
                .url(this.authCtx.baseUrl + path);
        requestBuilder.header("Content-Type", "application/json");
        requestBuilder.header("Authorization", "Bearer " + this.authCtx.accessToken);
        requestBuilder.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), data.toString()));
        JSONObject response = this.authCtx.send(requestBuilder);
        return jsonObjectToMap(response);
    }

    public Map<String, String> delete() {
        String method = "DELETE";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        // Construct the data as a JSON string
        JSONObject data = new JSONObject();
        data.put("id", "23a97920a07a40eb9f7e093b6398aec4");

        Request.Builder requestBuilder = new Request.Builder()
                .url(this.authCtx.baseUrl + path);
        requestBuilder.header("Content-Type", "application/json");
        requestBuilder.header("Authorization", "Bearer " + this.authCtx.accessToken);
        requestBuilder.delete(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), data.toString()));
        JSONObject response = this.authCtx.send(requestBuilder);
        return jsonObjectToMap(response);
    }

    private void validateDestination(String name, String destination, String type) {
        if (name.length() < 1 || name.length() > 32) {
            throw new ScannerError("Scan destination name too long.");
        }

        if (destination.length() < 4 || destination.length() > 544) {
            throw new ScannerError("Scan destination too long.");
        }

        if (!VALID_DESTINATION_TYPES.contains(type)) {
            throw new ScannerError("Invalid scan destination type " + type + ".");
        }
    }
}

class ScannerError extends RuntimeException {
    public ScannerError(String message) {
        super(message);
    }
}
