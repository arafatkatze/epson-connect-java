package com.epsonconnectjava;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
/**
 * The Client class provides functionality for interacting with the Epson Connect API.
 * It allows setting up an authentication context, deauthenticating, and obtaining Printer and Scanner objects.
 */
public class Client {

    /** Default base URL for the Epson Connect API. */
    private static final String EC_BASE_URL = "https://api.epsonconnect.com";
    /** Represents the authentication context. */
    private AuthCtx authCtx;

    /**
     * Initializes a new Client object with default baseUrl.
     *
     * @param printerEmail  Email associated with the printer.
     * @param clientId      Client ID for API authentication.
     * @param clientSecret  Client secret for API authentication.
     *
     * @throws ClientError If essential parameters (printerEmail, clientId, or clientSecret) are missing.
     */
    public Client(String printerEmail, String clientId, String clientSecret) {
        this(EC_BASE_URL, printerEmail, clientId, clientSecret, null);
    }

    /**
     * Initializes a new Client object.
     *
     * @param baseUrl       Base URL for the API. Falls back to the default if not provided.
     * @param printerEmail  Email associated with the printer.
     * @param clientId      Client ID for API authentication.
     * @param clientSecret  Client secret for API authentication.
     * @param env           A map containing environment variables.
     *
     * @throws ClientError If essential parameters (printerEmail, clientId, or clientSecret) are missing.
     */
    public Client(String baseUrl, String printerEmail, String clientId, String clientSecret, Map<String, String> env) {
        if (env == null) {
            env = new HashMap<>();
        }
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

        this.authCtx = new AuthCtx(baseUrl, printerEmail, clientId, clientSecret);
    }

    /**
     * Deauthenticates the client from the Epson Connect API.
     *
     * @throws IOException If there's an issue during deauthentication.
     */
    public void deauthenticate() throws IOException {
        this.authCtx.deauthenticate();
    }

    /**
     * Returns a new Printer object for further interactions with the API.
     *
     * @return A Printer object.
     */
    public Printer getPrinter() {
        return new Printer(this.authCtx);
    }

    /**
     * Returns a new Scanner object for further interactions with the API.
     *
     * @return A Scanner object.
     */
    public Scanner getScanner() {
        return new Scanner(this.authCtx);
    }

    /**
     * Custom exception class to handle client-related errors.
     */
    public static class ClientError extends RuntimeException {
        /**
         * Constructs a new ClientError with the specified detail message.
         *
         * @param message The detail message.
         */
        public ClientError(String message) {
            super(message);
        }
    }
}