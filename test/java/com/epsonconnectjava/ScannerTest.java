package epsonconnectjava;

import com.epsonconnectjava.Scanner;
import com.epsonconnectjava.AuthCtx;
import com.epsonconnectjava.http.HttpClient;
import com.epsonconnectjava.http.RealHttpClient;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.epsonconnectjava.Scanner;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ScannerTest {

    @Mock
    private HttpClient mockHttpClient;

    private AuthCtx authCtx, authRealCtx;
    private Scanner scanner;

    @BeforeEach
    public void setUp() throws Exception {


        authRealCtx = new AuthCtx( "https://api.epsonconnect.com",  "pdx3882hvp0q97@print.epsonconnect.com", "a243e42e187e469f8e9c6e2383b7e2e6", "PDLDVwcHI7eX4oL2jHGEdIgl0EK9iMdjNkXumi2tZIgaeyG5AKtGqgHQCEyNZGsR");
//        authRealCtx.auth();
        //   scanner = new Scanner(authCtx);
    }

    @Test
    public void testScanner() throws Exception {
        scanner = new Scanner(authRealCtx);
        
        scanner.list();

    }
}