package uk.co.asepstrath.bank;

import io.jooby.test.MockRouter;
import uk.co.asepstrath.bank.App;
import io.jooby.StatusCode;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import io.jooby.StatusCode;
import io.jooby.test.MockRouter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnitTest {


    @Test
    public void submit() {
        MockRouter router = new MockRouter(new App());
        router.get("/bank/Login", rsp ->{

        });
        router.get("/bank/submitForm", rsp -> {
            assertEquals("Employee data saved successfulLy", rsp.value());
            assertEquals(StatusCode.OK, rsp.getStatusCode());
        });
    }

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
        router.get("/bank/viewAccount", rsp->{

        });
    }

    @Test
    public void viewAllTransaction(){
        MockRouter router = new MockRouter(new App());
        router.get("/bank/viewAllTransactions", rsp->{

        });
    }

    @Test
    public void viewBusinessTransactions() throws SQLException {
        MockRouter router = new MockRouter(new App());
        router.get("/bank/viewBusinessTransactions", rsp->{

        });
    }
}
