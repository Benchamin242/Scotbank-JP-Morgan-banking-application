package uk.co.asepstrath.bank;

import io.jooby.StatusCode;
import io.jooby.test.JoobyTest;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JoobyTest(App.class)
public class IntegrationTest {
    /*
    Integration tests should be here
    Example can be found in example/IntegrationTest.java
     */
    static OkHttpClient client = new OkHttpClient();

    @Test
    public void shouldSayHi(int serverPort) throws IOException {
        Request req = new Request.Builder()
                .url("http://localhost:" + serverPort+"/example")
                .build();

        try (Response rsp = client.newCall(req).execute()) {
            assertEquals("Welcome to Jooby!", rsp.body().string());
            assertEquals(StatusCode.OK.value(), rsp.code());
        }
    }

}
