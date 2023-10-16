package com.epsonconnectjava;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A handle for managing scan destinations on a scanner device
 * Allows for creating, listing, updating and deleting destinations
 */
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
        this.path = "/api/1/scanning/scanners/" + authCtx.getDeviceId() + "/destinations";
    }

    /**
     * Updates destination name, destination and type if provided
     *
     * @param id          The ID of the destination
     * @param name        The name of the destination
     * @param destination The destination
     * @param type        The type of the destination
     * @return Returns JSONObject to Map after updating destination
     * @throws IOException If any IO error occurs
     */
    public Map<String, String> update(String id, String name, String destination, String type) throws IOException {
        Map<String, String> destCache = destinationCache.get(id);
        if (destCache == null) {
//            throw new ScannerError("Scan destination is not yet registered.");
        }
        validateDestination(name, destination, type);
        delete(id);

        // Construct the data as a JSON string
        JSONObject data = new JSONObject();
        data.put("id", id);
        data.put("alias_name", name != null ? name : destCache.get("alias_name"));
        data.put("type", type != null ? type : destCache.get("type"));
        data.put("destination", destination != null ? destination : destCache.get("destination"));

        Request.Builder requestBuilder = new Request.Builder()
                .url(this.authCtx.baseUrl + path);
        requestBuilder.header("Content-Type", "application/json");
        requestBuilder.header("Authorization", "Bearer " + this.authCtx.accessToken);
        requestBuilder.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), data.toString()));
        JSONObject response = this.authCtx.send(requestBuilder);

        destinationCache.put(id, jsonObjectToMap(response)); // Updating cache
        return jsonObjectToMap(response);
    }

    /**
     * Lists all scan destinations
     *
     * @return Returns JSONObject to Map after listing all scan destinations
     * @throws IOException If any IO error occurs
     */
    public Map<String, String> list() throws IOException {
        Request.Builder requestBuilder = new Request.Builder()
                .url(this.authCtx.baseUrl + path);
        requestBuilder.header("Content-Type", "application/json");
        requestBuilder.header("Authorization", "Bearer " + this.authCtx.accessToken);
        requestBuilder.get();
        JSONObject response = this.authCtx.send(requestBuilder);
        return jsonObjectToMap(response);
    }


    /**
     * Converts JSONObject to Map
     *
     * @param jsonObject the JSONObject to convert
     * @return Returns the converted Map
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

    /**
     * Adds a new scan destination
     *
     * @param name        The name of the destination
     * @param destination The destination
     * @param type        The type of the destination
     * @return Returns JSONObject to Map after adding scan destination
     * @throws IOException If any IO error occurs
     */
    public Map<String, String> add(String name, String destination, String type) throws IOException {
        validateDestination(name, destination, type);

        // Construct the data as a JSON string
        JSONObject data = new JSONObject();
        data.put("alias_name", name);
        data.put("type", type);
        data.put("destination", destination);

        Request.Builder requestBuilder = new Request.Builder()
                .url(this.authCtx.baseUrl + path);
        requestBuilder.header("Content-Type", "application/json");
        requestBuilder.header("Authorization", "Bearer " + this.authCtx.accessToken);
        requestBuilder.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), data.toString()));
        JSONObject response = this.authCtx.send(requestBuilder);
        Map<String, String> responseMap = jsonObjectToMap(response);
        // Assuming the response contains an 'id' field which acts as the unique identifier
        if (responseMap.containsKey("id")) {
            destinationCache.put(responseMap.get("id"), responseMap);
        }
        return responseMap;
    }

    /**
     * Deletes a scan destination
     *
     * @param id the id of the destination to be deleted
     * @return Returns JSONObject to Map after deleting scan destination
     * @throws IOException if any IO error occurs
     */
    public Map<String, String> delete(String id) throws IOException {
        // Construct the data as a JSON string
        JSONObject data = new JSONObject();
        data.put("id", id);

        Request.Builder requestBuilder = new Request.Builder()
                .url(this.authCtx.baseUrl + path);
        requestBuilder.header("Content-Type", "application/json");
        requestBuilder.header("Authorization", "Bearer " + this.authCtx.accessToken);
        requestBuilder.delete(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), data.toString()));
        JSONObject response = this.authCtx.send(requestBuilder);

        destinationCache.remove(id); // Removing from cache
        return jsonObjectToMap(response);
    }

    /**
     * Validates destination name, destination and type length and type respectively
     *
     * @param name        The name to validate
     * @param destination The destination to validate
     * @param type        The type to validate
     */
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

/**
 * Represents an exception for the Scanner
 */
class ScannerError extends RuntimeException {
    /**
     * Constructs a new scanner error exception with the specified detail message
     *
     * @param message the detail message
     */
    public ScannerError(String message) {
        super(message);
    }
}
