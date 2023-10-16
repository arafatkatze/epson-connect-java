package com.epsonconnectjava;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a Printer that interacts with an external API to perform various printer operations.
 */
public class Printer {
    // Define valid destination types for the scanner
    private static final Set<String> VALID_DESTINATION_TYPES;
    private static final Set<String> VALID_EXTENSIONS;
    private static final Set<String> VALID_OPERATORS;

    static {
        VALID_DESTINATION_TYPES = new HashSet<>();
        VALID_DESTINATION_TYPES.add("mail");
        VALID_DESTINATION_TYPES.add("url");
    }

    static {
        VALID_EXTENSIONS = new HashSet<>();
        VALID_EXTENSIONS.add("doc");
        VALID_EXTENSIONS.add("docx");
        VALID_EXTENSIONS.add("xls");
        VALID_EXTENSIONS.add("xlsx");
        VALID_EXTENSIONS.add("ppt");
        VALID_EXTENSIONS.add("pptx");
        VALID_EXTENSIONS.add("pdf");
        VALID_EXTENSIONS.add("jpeg");
        VALID_EXTENSIONS.add("jpg");
        VALID_EXTENSIONS.add("bmp");
        VALID_EXTENSIONS.add("gif");
        VALID_EXTENSIONS.add("png");
        VALID_EXTENSIONS.add("tiff");

        VALID_OPERATORS = new HashSet<>();
        VALID_OPERATORS.add("user");
        VALID_OPERATORS.add("operator");
    }

    private AuthCtx authCtx;

    /**
     * Constructor for the Printer class.
     *
     * @param authCtx The authentication context containing necessary credentials and tokens.
     */
    public Printer(AuthCtx authCtx) {
        this.authCtx = authCtx;
        this.authCtx.auth();
    }

    /**
     * Retrieves information about the printer.
     *
     * @return A map containing details about the printer.
     * @throws IOException If an error occurs during the API request.
     */
    public Map<String, String> info() throws IOException {
        String path = "/api/1/printing/printers/" + authCtx.getDeviceId();
        Request.Builder requestBuilder = new Request.Builder()
                .url(this.authCtx.baseUrl + path);

        requestBuilder.header("Content-Type", "application/json");
        requestBuilder.header("Authorization", "Bearer " + this.authCtx.accessToken);
        requestBuilder.get();

        JSONObject response = this.authCtx.send(requestBuilder);
        return jsonObjectToMap(response);
    }

    /**
     * Retrieves the device ID.
     *
     * @return A string representing the device ID.
     */
    public String deviceId() {
        return authCtx.getDeviceId();
    }

    /**
     * Retrieves information about a particular job.
     *
     * @param jobId The ID of the print job to be executed.
     * @return A map containing details about the job.
     * @throws IOException If an error occurs during the API request.
     */
    public Map<String, String> jobInfo(String jobId) throws IOException {
        String path = "/api/1/printing/printers/" + authCtx.getDeviceId() + "/jobs/" + jobId;
        Request.Builder requestBuilder = new Request.Builder()
                .url(this.authCtx.baseUrl + path);

        requestBuilder.header("Content-Type", "application/json");
        requestBuilder.header("Authorization", "Bearer " + this.authCtx.accessToken);
        requestBuilder.get();

        JSONObject response = this.authCtx.send(requestBuilder);
        return jsonObjectToMap(response);
    }

    /**
     * Retrieves the print settings.
     *
     * @return A JSONObject containing the print settings.
     * @throws IOException If an error occurs during the API request.
     */
    public JSONObject printSetting() throws IOException {
        String method = "POST";
        String path = "/api/1/printing/printers/" + this.authCtx.getDeviceId() + "/jobs";
        Map<String, Object> settings = PrintSetting.mergeWithDefaultSettings(null);
        PrintSetting.validateSettings(settings);
        String jsonBody = new JSONObject(settings).toString();
        Request.Builder requestBuilder = new Request.Builder()
                .url(this.authCtx.baseUrl + path)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + this.authCtx.accessToken)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody));

        return this.authCtx.send(requestBuilder);
    }

    /**
     * Executes a print job using the specified job ID.
     *
     * @param jobId The ID of the print job to be executed.
     * @throws IOException If an error occurs during the API request.
     */
    public void executePrint(String jobId) throws IOException {
        String method = "POST";
        String path = "/api/1/printing/printers/" + this.authCtx.getDeviceId() + "/jobs/" + jobId + "/print";

        Request.Builder requestBuilder = new Request.Builder()
                .url(this.authCtx.baseUrl + path)
                .header("Authorization", "Bearer " + this.authCtx.accessToken)
                .post(RequestBody.create(null, new byte[0]));  // Empty POST body

        this.authCtx.send(requestBuilder);
    }

    /**
     * Initiates a print operation for the specified file path.
     *
     * @param filePath The path to the file to be printed.
     * @return The job ID of the initiated print operation.
     * @throws IOException        If an error occurs during the API request.
     * @throws URISyntaxException If there's an error in URI parsing or construction.
     */
    public String print(String filePath) throws IOException, URISyntaxException {
        // Create a print job
        JSONObject jobData = printSetting();
        // Upload file for printing
        uploadFile(jobData.getString("upload_uri"), Paths.get(filePath), "document");
        executePrint(jobData.getString("id"));
        // Return the Job ID
        return jobData.getString("id");
    }

    /**
     * Retrieves the file extension from the given file name.
     *
     * @param fileName The name of the file.
     * @return The file extension or an empty string if no extension is found.
     */
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) return ""; // No extension
        return fileName.substring(dotIndex + 1);
    }

    /**
     * Uploads a file to a specified URI for printing.
     *
     * @param uploadUri The URI to which the file should be uploaded.
     * @param filePath  The path to the file to be uploaded.
     * @param printMode The mode in which the file should be printed.
     * @throws IOException        If an error occurs during the API request.
     * @throws URISyntaxException If there's an error in URI parsing or construction.
     */
    public void uploadFile(String uploadUri, Path filePath, String printMode) throws IOException, URISyntaxException {
        // 1. Extract and validate file extension
        String extension = getFileExtension(filePath.toString()).toLowerCase();
        if (!VALID_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(extension + " is not a valid printing extension.");
        }

        // Get extension from file path
        URI uri = new URI(uploadUri);
        URI BaseUri = new URI(this.authCtx.baseUrl);
        String newQuery = uri.getQuery() + "&File=1." + extension;
        URI newUri = new URI(uri.getScheme(), BaseUri.getAuthority(), uri.getPath(), newQuery, uri.getFragment());
        String path = newUri.toString();
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

    /**
     * Converts a JSONObject into a Map.
     *
     * @param jsonObject The JSONObject to be converted.
     * @return A map representation of the given JSONObject.
     */
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
