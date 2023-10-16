package epsonconnectjava;

import com.epsonconnectjava.AuthCtx;
import com.epsonconnectjava.Printer;
import com.epsonconnectjava.http.HttpClient;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PrinterTest {

    @Mock
    private HttpClient mockHttpClient;

    private AuthCtx authCtx, authRealCtx;
    private Printer printer;
    @Mock
    private AuthCtx mockAuthCtx;

    @BeforeEach
    public void setUp() {
        mockAuthCtx = Mockito.mock(AuthCtx.class);
        mockAuthCtx.baseUrl = "https://baseUrl/";

        printer = new Printer(mockAuthCtx);
    }

    @Test
    public void testJobInfo() throws IOException {
        String expectedJobId = "testJobId";
        String expectedUrl = "https://baseUrl/api/1/printing/printers/testDeviceId/jobs/" + expectedJobId;
        JSONObject mockResponse = new JSONObject();
        mockResponse.put("jobId", expectedJobId);
        mockResponse.put("status", "completed");

        when(mockAuthCtx.getDeviceId()).thenReturn("testDeviceId");
        when(mockAuthCtx.send(any())).thenReturn(mockResponse);

        Map<String, String> result = printer.jobInfo(expectedJobId);

        assertEquals(expectedJobId, result.get("jobId"));
        assertEquals("completed", result.get("status"));
    }

    @Test
    public void testPrintSetting() throws IOException {
        JSONObject mockResponse = new JSONObject();
        mockResponse.put("setting", "testSetting");

        when(mockAuthCtx.send(any())).thenReturn(mockResponse);

        JSONObject result = printer.printSetting();

        assertEquals("testSetting", result.get("setting"));
    }

    @Test
    public void testExecutePrint() throws IOException {
        String expectedJobId = "testJobId";

        printer.executePrint(expectedJobId);

        verify(mockAuthCtx, times(1)).send(any());
    }

    @Test
    public void testPrint() throws IOException, URISyntaxException {
        JSONObject mockResponse = new JSONObject();
        mockResponse.put("upload_uri", "https://baseUrl");
        mockResponse.put("id", "testJobId");

        when(mockAuthCtx.send(any())).thenReturn(mockResponse);

        String result = printer.print("testFilePath.pdf");

        assertEquals("testJobId", result);
    }
}