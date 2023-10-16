package com.epsonconnectjava.http;

import okhttp3.Request;
import org.json.JSONObject;

import java.io.IOException;

public interface HttpClient {
    JSONObject send(Request.Builder requestBuilder) throws IOException;
}
