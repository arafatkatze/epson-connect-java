package epsonconnectjava;

import com.epsonconnectjava.AuthCtx;
import com.epsonconnectjava.http.HttpClient;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;

public class AuthCtxTest {

    @Mock
    private HttpClient mockHttpClient;

    private AuthCtx authCtx;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAuthWithNewToken() throws Exception {
        // Arrange
        JSONObject response = new JSONObject();
        response.put("access_token", "newAccessToken");
        response.put("refresh_token", "newRefreshToken");
        response.put("expires_in", 3600);  // 1 hour
        response.put("subject_id", "newSubjectId");
        Mockito.when(mockHttpClient.send(any())).thenReturn(response);

        authCtx = new AuthCtx(mockHttpClient, "https://baseUrl", "testPrinterEmail", "testClientId", "testClientSecret");
        // Act
        authCtx.auth();

        // Assert
        assertEquals("newAccessToken", authCtx.getAccessToken());
        assertEquals("newRefreshToken", authCtx.getRefreshToken());
    }

    @Test
    public void testAuthWithRefreshToken() throws Exception {
        // Arrange
        JSONObject response = new JSONObject();
        response.put("access_token", "refreshedAccessToken");
        response.put("refresh_token", "refreshedAccessToken");
        response.put("expires_in", 3600);  // 1 hour
        response.put("subject_id", "newSubjectId");
        Mockito.when(mockHttpClient.send(any())).thenReturn(response);

        authCtx = new AuthCtx(mockHttpClient, "https://baseUrl", "testPrinterEmail", "testClientId", "testClientSecret");
        authCtx.setAccessToken("oldAccessToken");
        authCtx.setRefreshToken("oldRefreshToken");
        authCtx.setExpiresAt(new Date(System.currentTimeMillis() - 1000)); // Expired token


        // Act
        authCtx.auth();

        // Assert
        assertEquals("refreshedAccessToken", authCtx.getAccessToken());
        assertEquals("oldRefreshToken", authCtx.getRefreshToken());  // Should remain unchanged
    }
}
