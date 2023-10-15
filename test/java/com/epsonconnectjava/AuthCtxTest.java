package epsonconnectjava;

import com.epsonconnectjava.AuthCtx;
import com.epsonconnectjava.http.HttpClient;
import com.epsonconnectjava.http.RealHttpClient;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthCtxTest {

    @Mock
    private HttpClient mockHttpClient;

    private AuthCtx authCtx, authRealCtx;

    @BeforeEach
    public void setUp() throws Exception {

        MockitoAnnotations.openMocks(this);
        authCtx = new AuthCtx(mockHttpClient, "https://baseUrl", "testPrinterEmail", "testClientId", "testClientSecret");
    }

    @Test
    public void testAuthWithNewToken() throws Exception {
        // Arrange
        JSONObject response = new JSONObject();
        response.put("refresh_token", "newRefreshToken");
        response.put("expires_in", 3600);
        response.put("access_token", "newAccessToken");
        response.put("subject_id", "newSubjectId");
        when(mockHttpClient.send(anyString(), anyString(), anyMap(), anyMap())).thenReturn(response);


        System.out.println("Running Auth methoding for love");
        // Act
        authCtx.auth();

        // Verify that the HttpClient's send method was called once
        verify(mockHttpClient, times(1)).send(anyString(), anyString(), anyMap(), anyMap());

        authRealCtx = new AuthCtx( "https://api.epsonconnect.com",  "pdx3882hvp0q97@print.epsonconnect.com", "a243e42e187e469f8e9c6e2383b7e2e6", "PDLDVwcHI7eX4oL2jHGEdIgl0EK9iMdjNkXumi2tZIgaeyG5AKtGqgHQCEyNZGsR");
        authRealCtx.auth();
    }

    // ... You can continue with other tests, such as testing the refresh logic, testing the error scenarios, etc.
}
