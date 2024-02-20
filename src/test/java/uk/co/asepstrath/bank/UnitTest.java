package uk.co.asepstrath.bank;

import io.jooby.test.MockRouter;
import uk.co.asepstrath.bank.App;
import io.jooby.StatusCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnitTest {

    /*
    Unit tests should be here
    Example can be found in example/UnitTest.java
     */

    @Test
    public void welcome(){
        MockRouter router = new MockRouter(new App());
        router.get("/bank", rsp->{
            assertEquals("Welcome to Jooby!", rsp.value());
            assertEquals(StatusCode.OK, rsp.getStatusCode());
        });
    }
    @Test
    public void hello(){
        MockRouter router = new MockRouter(new App());
        router.get("/bank/hello", rsp->{
            assertEquals("Hello", rsp.value());
            assertEquals(StatusCode.OK, rsp.getStatusCode());
        });
    }
    @Test
    public void viewAccount() {
        MockRouter router = new MockRouter(new App());
        router.get("/bank", rsp->{

        });
    }

}
