package com.epsonconnectjava.http;
import okhttp3.Request;
import org.json.JSONObject;  // Assuming you're using the org.json library
import java.util.Map;
public interface HttpClient {
    JSONObject send(Request.Builder requestBuilder);
}
