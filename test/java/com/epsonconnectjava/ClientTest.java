package epsonconnectjava;

import com.epsonconnectjava.Client;
import com.epsonconnectjava.Printer;
import com.epsonconnectjava.Scanner;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClientTest {

    private Client client;
    private Map<String, String> mockEnv;

    @Test
    public void testClientInitializationWithAllParams() {
        client = new Client("http://baseUrl", "printerEmail", "clientId", "clientSecret", mockEnv);
        assertNotNull(client);
    }

    @Test
    public void testClientInitializationWithMissingPrinterEmail() {
        assertThrows(Client.ClientError.class, () -> {
            new Client("http://baseUrl", null, "clientId", "clientSecret", mockEnv);
        });
    }

    @Test
    public void testClientInitializationWithMissingClientId() {
        assertThrows(Client.ClientError.class, () -> {
            new Client("http://baseUrl", "printerEmail", null, "clientSecret", mockEnv);
        });
    }

    @Test
    public void testClientInitializationWithMissingClientSecret() {
        assertThrows(Client.ClientError.class, () -> {
            new Client("http://baseUrl", "printerEmail", "clientId", null, mockEnv);
        });
    }


    @Test
    public void testGetPrinter() {
        client = new Client("http://baseUrl", "printerEmail", "clientId", "clientSecret", mockEnv);
        Printer printer = client.getPrinter();
        assertNotNull(printer);
    }

    @Test
    public void testGetScanner() {
        client = new Client("http://baseUrl", "printerEmail", "clientId", "clientSecret", mockEnv);
        Scanner scanner = client.getScanner();
        assertNotNull(scanner);
    }
}
