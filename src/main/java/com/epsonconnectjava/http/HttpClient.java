package com.epsonconnectjava.http;
import org.json.JSONObject;  // Assuming you're using the org.json library
import java.util.Map;
public interface HttpClient {
    JSONObject send(String method, String path, Map<String, String> data, Map<String, String> headers);
}
