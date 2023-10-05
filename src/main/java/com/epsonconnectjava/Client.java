package com.epsonconnectjava;

import java.util.Optional;
import com.epsonconnectjava.AuthCtx;
import java.util.Map;
import java.util.Optional;

public class Client {

    private static final String EC_BASE_URL = "https://api.epsonconnect.com";

    private AuthCtx authCtx;

    public Client(String baseUrl, String printerEmail, String clientId, String clientSecret, Map<String, String> env) {
        baseUrl = Optional.ofNullable(baseUrl).orElse(EC_BASE_URL);

        printerEmail = Optional.ofNullable(printerEmail).orElse(env.get("EPSON_CONNECT_API_PRINTER_EMAIL"));
        if (printerEmail == null || printerEmail.isEmpty()) {
            throw new ClientError("Printer Email cannot be empty");
        }

        clientId = Optional.ofNullable(clientId).orElse(env.get("EPSON_CONNECT_API_CLIENT_ID"));
        if (clientId == null || clientId.isEmpty()) {
            throw new ClientError("Client ID cannot be empty");
        }

        clientSecret = Optional.ofNullable(clientSecret).orElse(env.get("EPSON_CONNECT_API_CLIENT_SECRET"));
        if (clientSecret == null || clientSecret.isEmpty()) {
            throw new ClientError("Client Secret cannot be empty");
        }

        // this.authCtx = new AuthCtx(baseUrl, printerEmail, clientId, clientSecret);
    }


    public void deauthenticate() {
        this.authCtx.deauthenticate();
    }

    // public Printer getPrinter() {
    //     return new Printer(this.authCtx);
    // }

    // public Scanner getScanner() {
    //     return new Scanner(this.authCtx);
    // }

    public static class ClientError extends RuntimeException {
        public ClientError(String message) {
            super(message);
        }
    }
}

// Assuming you also have the equivalent Printer and Scanner classes defined as in Python.
