package uk.co.asepstrath.bank;

import io.jooby.ModelAndView;
import io.jooby.test.MockRouter;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import uk.co.asepstrath.bank.App;
import io.jooby.StatusCode;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
        Map<String, Object> map = new HashMap<>();
        MockRouter router = new MockRouter(new App());
        router.get("/bank/viewAccount", rsp->{
            assertEquals(new ModelAndView("simpleDetails.hbs", map).toString(), rsp.value().toString());
        });
    }

    @Test
    public void apiInteraction(){

        HttpResponse<Account[]> help = Unirest.get("https://api.asep-strath.co.uk/api/accounts").asObject(Account[].class);

        assertEquals(200, help.getStatus());
        Account[] t = help.getBody();
        assertEquals("01b02232-eeff-4294-aad0-c3cdbbbf773c Miss Lavina Waelchi null false", t[0].toString());
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
