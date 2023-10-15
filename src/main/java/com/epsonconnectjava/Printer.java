package com.epsonconnectjava;

import com.epsonconnectjava.AuthCtx;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import okhttp3.*;
import org.json.JSONObject;
public class Printer {
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
    public Printer(AuthCtx authCtx) {
        this.authCtx = authCtx;
        this.authCtx.auth();
        this.path = "/api/1/printing/printers/" + authCtx.getDeviceId() + "/destinations";
    }
    public Map<String, String> info() {
        String method = "GET";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        path = "/api/1/printing/printers/" + authCtx.getDeviceId();

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
}
