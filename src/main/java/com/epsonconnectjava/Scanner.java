package com.epsonconnectjava;

import com.epsonconnectjava.AuthCtx;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
      
      // The third parameter (data) is set to null as we are not sending any data in a GET request
      JSONObject response = this.authCtx.sendGet(method, path, null, headers);
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

    // public Map<String, String> add(String name, String destination, String type) {
    //     String method = "POST";

    //     validateDestination(name, destination, type);

    //     Map<String, String> data = new HashMap<>();
    //     data.put("alias_name", name);
    //     data.put("type", type);
    //     data.put("destination", destination);

    //     Map<String, String> resp = authCtx.send(method, path, data);
    //     destinationCache.put(resp.get("id"), resp);
    //     return resp;
    // }

    // public Map<String, String> update(String id, String name, String destination, String type) {
    //     String method = "POST";

    //     Map<String, String> destCache = destinationCache.get(id);
    //     if (destCache == null) {
    //         throw new ScannerError("Scan destination is not yet registered.");
    //     }

    //     validateDestination(name, destination, type);

    //     Map<String, String> data = new HashMap<>();
    //     data.put("id", id);
    //     data.put("alias_name", name != null ? name : destCache.get("alias_name"));
    //     data.put("type", type != null ? type : destCache.get("type"));
    //     data.put("destination", destination != null ? destination : destCache.get("destination"));

    //     Map<String, String> resp = authCtx.send(method, path, data);
    //     destinationCache.put(id, resp);
    //     return resp;
    // }

    // public void remove(String id) {
    //     String method = "DELETE";

    //     Map<String, String> data = new HashMap<>();
    //     data.put("id", id);

    //     authCtx.send(method, path, data);
    //     destinationCache.remove(id);
    // }

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
