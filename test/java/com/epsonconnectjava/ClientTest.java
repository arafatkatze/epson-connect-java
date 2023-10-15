package epsonconnectjava;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import org.mockito.MockedStatic;
import org.junit.jupiter.api.BeforeEach;
import java.util.HashMap;
import java.util.Map;
import com.epsonconnectjava.Client;



public class ClientTest {

    private Client client;
    private Map<String, String> mockEnv;

    // This method sets up the environment variables to mock them for testing purposes
    @BeforeEach
    public void setup() {
        mockEnv = new HashMap<>();
        mockEnv.put("EPSON_CONNECT_API_PRINTER_EMAIL", "testPrinterEmail");
        mockEnv.put("EPSON_CONNECT_API_CLIENT_ID", "testClientId");
        mockEnv.put("EPSON_CONNECT_API_CLIENT_SECRET", "testClientSecret");
        client = new Client("https://baseUrl", "testPrinterEmail", "testClientId", "testClientSecret", mockEnv);
    }

    @Test
    public void testConstructorWithAllParams() {
        client = new Client("https://testBaseUrl", "testPrinterEmail", "testClientId", "testClientSecret", mockEnv);
        assertNotNull(client);
    }

//    @Test
//    public void testConstructorWithMissingEmail() {
//        Exception exception = assertThrows(Client.ClientError.class, () -> {
//            new Client("https://testBaseUrl", null, "testClientId", "testClientSecret", mockEnv);
//        });
//
//        String expectedMessage = "Printer Email cannot be empty";
//        String actualMessage = exception.getMessage();
//        assertTrue(actualMessage.contains(expectedMessage));
//    }
//
//    @Test
//    public void testConstructorWithMissingClientId() {
//        Exception exception = assertThrows(Client.ClientError.class, () -> {
//            new Client("https://testBaseUrl", "testPrinterEmail", null, "testClientSecret", mockEnv);
//        });
//
//        String expectedMessage = "Client ID cannot be empty";
//        String actualMessage = exception.getMessage();
//        assertTrue(actualMessage.contains(expectedMessage));
//    }
//
//    @Test
//    public void testConstructorWithMissingClientSecret() {
//        Exception exception = assertThrows(Client.ClientError.class, () -> {
//            new Client("https://testBaseUrl", "testPrinterEmail", "testClientId", null, mockEnv);
//        });
//
//        String expectedMessage = "Client Secret cannot be empty";
//        String actualMessage = exception.getMessage();
//        assertTrue(actualMessage.contains(expectedMessage));
//    }

    // ... Add more tests as required

}
