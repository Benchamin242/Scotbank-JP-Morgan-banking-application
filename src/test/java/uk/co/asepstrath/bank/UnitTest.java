package uk.co.asepstrath.bank;

import io.jooby.*;
import io.jooby.exception.StatusCodeException;
import io.jooby.test.MockContext;
import io.jooby.test.MockRouter;
import io.jooby.test.MockSession;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import org.h2.mvstore.tx.Transaction;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.App;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import io.jooby.test.MockRouter;
import org.junit.jupiter.api.Test;
import uk.co.asepstrath.bank.bank.BankController;
import io.jooby.ModelAndView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;


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
    public void loginTest(){
        BankController bankController = new BankController(null, null);
        ModelAndView modelAndView = bankController.login();

        assertEquals("loginView.hbs", modelAndView.getView(), "View name should be 'loginView.hbs'");

        Map<String, Object> expectedModel = new HashMap<>();
        expectedModel.put("name", "Please");
        assertEquals(expectedModel, modelAndView.getModel(), "Model should contain 'name' with value 'Please'");

    }

    @Test
    public void apiInteraction(){

        HttpResponse<Account[]> help = Unirest.get("https://api.asep-strath.co.uk/api/accounts").asObject(Account[].class);

        assertEquals(200, help.getStatus());
        Account[] t = help.getBody();
        assertEquals("01b02232-eeff-4294-aad0-c3cdbbbf773c Miss Lavina Waelchi null false", t[0].toString());
    }

    @Test
    public void signup(){
        App app = new App();
        BankController bankController = app.bankController;
        ModelAndView signupModel = bankController.signup();

        Map<String, Object> model = new HashMap<>();
        assertEquals(model,signupModel.getModel());

    }

    @Test
    public void transactionsAPI(){
        //HttpResponse<Transactions[]> help = Unirest.get("https://api.asep-strath.co.uk/api/transactions").asObject(Transactions[].class);
        HttpResponse<Transactions[]> help = Unirest.get("https://api.asep-strath.co.uk/api/transactions")
                .queryString("Page", 0)
                .queryString("Size", 5)
                .asObject(Transactions[].class);

        //assertNotNull(help.getBody());
        assertEquals(200, help.getStatus());
        Transactions[] transactionsList = help.getBody();

        if(transactionsList != null) {
            for (Transactions transaction : transactionsList) {
                System.out.println("transaction: " + transaction.toString());

            }
        }

    }
    @Test
    public void authenticationAPI(){
        HttpResponse<JsonNode> help = Unirest.post("https://api.asep-strath.co.uk/0auth2/token").asObject(JsonNode.class);

        assertEquals(200, help.getStatus());

    }

    @Test
    public void businessAPI(){
        HttpResponse<JsonNode> help = Unirest.post("https://api.asep-strath.co.uk/api/businesses").asObject(JsonNode.class); // change from JsonNode to business class

        assertEquals(200, help.getStatus());
    }




/*
    @Test
    public void viewAccount() throws InstantiationException, IllegalAccessException {
        Context ctx = new MockContext();
        MockSession session = (MockSession) ctx.session();
        session.put("id", "test_id");



        BankController bankController = new BankController(null, null);
        ModelAndView modelAndView = bankController.viewAccounts(ctx);

        assertEquals("simpleDetails.hbs", modelAndView.getView(), "View name should be 'simpleDetails.hbs'");

        Map<String, Object> expectedModel = new HashMap<>();
        expectedModel.put("accountNum", 1);
        expectedModel.put("id", "test_id");

        Map<String, Object> actualModel = modelAndView.getModel();
        for (Map.Entry<String, Object> entry : expectedModel.entrySet()) {
            String key = entry.getKey();
            Object expectedValue = entry.getValue();
            assertTrue(actualModel.containsKey(key), "Model should contain key: " + key);
            assertEquals(expectedValue, actualModel.get(key), "Model value for key " + key + " should match expected value");
        }
    }



    @Test
    public void checkIfloggedin(){
        Context ctx = new MockContext();

        Session session = new MockSession();
        session.put("id", "test_id");



        BankController bankController = new BankController(null, null);

        String result = bankController.checkIfLoggedIn(null);

        assertNotNull(result);
        assertEquals("test_id", result, "Returned ID should match session ID");
    }

    @Test
    public void AllTransactions(){
        MockContext ctx = new MockContext();
        App app = new App();
        MockRouter router = new MockRouter(app);

        DataSource ds = app.require(DataSource.class);
        Logger log = app.getLog();
        BankController bankController = new BankController(ds,log);

        ModelAndView modelAndView = bankController.viewAllTransactions(ctx);
        Map<String, Object> model = new HashMap<>();

        model.put("paidTo", "test");
        model.put("amount", "test");

        assertNotEquals(model,modelAndView.getModel() );


    }

    @Test
    public void BusinessTransactions(){
        BankController bankController = new BankController(null, null);
        ModelAndView modelAndView = bankController.viewBusinessTransactions(null);
    }

     */
}
