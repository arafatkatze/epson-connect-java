package epsonconnectjava;

import com.epsonconnectjava.AuthCtx;
import com.epsonconnectjava.Scanner;
import com.epsonconnectjava.http.HttpClient;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ScannerTest {

    @Mock
    private HttpClient mockHttpClient;

    private AuthCtx mockAuthCtx;
    private Scanner scanner;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockAuthCtx = Mockito.mock(AuthCtx.class);
        mockAuthCtx.baseUrl = ("http://mockedbaseurl.com");
        mockAuthCtx.accessToken = ("mockedAccessToken");
        scanner = new Scanner(mockAuthCtx);
    }

    @Test
    public void testList() throws IOException {
        // Arrange
        JSONObject mockedResponse = new JSONObject();
        mockedResponse.put("id", "mockedId");
        mockedResponse.put("alias_name", "mockedName");
        mockedResponse.put("type", "mockedType");
        mockedResponse.put("destination", "mockedDestination");

        when(mockAuthCtx.send(any())).thenReturn(mockedResponse);

        // Act
        Map<String, String> result = scanner.list();

        // Assert
        assertEquals("mockedId", result.get("id"));
        verify(mockAuthCtx, times(1)).send(any());
    }
}