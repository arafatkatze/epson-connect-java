package com.epsonconnectjava;

import com.epsonconnectjava.AuthCtx;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import okhttp3.*;
import org.json.JSONObject;
import java.nio.file.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import okhttp3.*;
import java.io.File;

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

    public JSONObject printSetting()  {
        String method = "POST";
        String path = "/api/1/printing/printers/" + this.authCtx.getDeviceId() + "/jobs";

//        validateSettings(settings); // Assuming you have this method in Java

        // Convert the settings map to a JSON string for request body
        Map<String, String> settings = new HashMap<>();
        settings.put("job_name", "job-ewqrEw");
        settings.put("print_mode", "document");
        String jsonBody = new JSONObject(settings).toString();

        Request.Builder requestBuilder = new Request.Builder()
                .url(this.authCtx.baseUrl + path)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + this.authCtx.accessToken)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody));

        return this.authCtx.send(requestBuilder);
    }

    public void executePrint(String jobId) throws IOException {
        String method = "POST";
        String path = "/api/1/printing/printers/" + this.authCtx.getDeviceId() + "/jobs/" + jobId + "/print";

        Request.Builder requestBuilder = new Request.Builder()
                .url(this.authCtx.baseUrl + path)
                .header("Authorization", "Bearer " + this.authCtx.accessToken)
                .post(RequestBody.create(null, new byte[0]));  // Empty POST body

        this.authCtx.send(requestBuilder);
    }

    public String print(String filePath) throws IOException, URISyntaxException {
        // Create a print job
        JSONObject jobData = printSetting();

        // Upload file for printing
        uploadFile(jobData.getString("upload_uri"), Paths.get(filePath), "document");

        executePrint(jobData.getString("id"));

        // Return the Job ID
        return jobData.getString("id");
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) return ""; // No extension
        return fileName.substring(dotIndex + 1);
    }

    public void uploadFile(String uploadUri, Path filePath, String printMode) throws IOException, URISyntaxException {
        // 1. Extract and validate file extension
        String extension = getFileExtension(filePath.toString()).toLowerCase();
//        if (!VALID_EXTENSIONS.contains(extension)) {
//            throw new IllegalArgumentException(extension + " is not a valid printing extension.");
//        }

        // Get extension from file path
        URI uri = new URI(uploadUri);
        String newQuery =  uri.getQuery() + "&File=1." + extension;
        URI newUri = new URI(uri.getScheme(), this.authCtx.baseUrl, uri.getPath(), newQuery, uri.getFragment());
        String pathd = newUri.toString();
        String path = "https://api.epsonconnect.com/c33fe124ef80c3b13670be27a6b0bcd7/v1/storage/PostData?" + newQuery;


        // 3. Determine content type
        String contentType = "application/octet-stream";
        if ("photo".equalsIgnoreCase(printMode)) {
            contentType = "image/jpeg";
        }


        // 4. Read file data
        File file = new File(String.valueOf(filePath));

        // 5. Set headers
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);

        // 6. Send the file for upload using POST request
        Request.Builder requestBuilder = new Request.Builder()
                .url(path)
                .header("Content-Length", "4911")
                .header("Content-Type", "application/octet-stream")
                .header("Authorization", "Bearer " + this.authCtx.accessToken)
                .post(requestBody);

        this.authCtx.send(requestBuilder);
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
