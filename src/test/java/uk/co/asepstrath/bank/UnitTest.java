package uk.co.asepstrath.bank;

import io.jooby.*;
import io.jooby.exception.StatusCodeException;
import io.jooby.test.MockContext;
import io.jooby.test.MockRouter;
import io.jooby.test.MockSession;
import kong.unirest.core.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.h2.mvstore.tx.Transaction;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import uk.co.asepstrath.bank.App;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.*;

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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.jupiter.api.Assertions.*;
import static uk.co.asepstrath.bank.IntegrationTest.client;


public class UnitTest {



    static OkHttpClient cline = new OkHttpClient();
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

  /*
    public void viewAllAccountTest() {
        BankController bankController = new BankController(null, null);
        Session session = new MockSession();

        ModelAndView modelAndView = bankController.viewAllAccounts(session);
        assertEquals();
    }
*/

    @Test
    public void apiInteraction(){
        String user = "scotbank";
        String password = "this1password2is3not4secure";

        // Send POST request to fetch OAuth2 token
        HttpResponse<authToken> response = Unirest.post("https://api.asep-strath.co.uk/oauth2/token")
                .field("grant_type", "client_credentials") // defaults to  .header("accept", "application/x-www-form-urlencoded") so no need to include
                .basicAuth(user,password)
                //.header("Authorization", "Bearer "+ new String(encodedBytes).concat("="))
                .asObject(authToken.class);
        authToken auth = response.getBody();



        HttpResponse<Account[]> help = Unirest.get("https://api.asep-strath.co.uk/api/accounts")
                .header("Authorization", "Bearer " + auth.access_token)
                .queryString("include", "cardDetails,postcode")
                .asObject(Account[].class);

        assertEquals(200, help.getStatus());
        Account[] t = help.getBody();
        System.out.println(t[1].toString());
        assertEquals("635e583f-0af2-47cb-9625-5b66ba30e188 Miss Lavina Waelchi 544.91 false EH3 9HU 0", t[0].toString());
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

        String help = Unirest.get("https://api.asep-strath.co.uk/api/transactions")
                .queryString("page", 0)
                .queryString("size", 5)
                .asString().getBody();

        try  {
            //.asObject(Transactions[].class);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(help));
            Document document = documentBuilder.parse(is);

            NodeList type = document.getElementsByTagName("type");
            NodeList amount = document.getElementsByTagName("amount");
            NodeList to = document.getElementsByTagName("to");
            NodeList from = document.getElementsByTagName("from");

            assertEquals("PAYMENT",type.item(0).getTextContent());
            assertEquals("170.00", amount.item(0).getTextContent());
            assertEquals("EE", to.item(0).getTextContent());
            assertEquals("3afedfd7-dfe0-468d-b79a-1a8f9db3497c", from.item(0).getTextContent());

            for(int i = 0; i < type.getLength(); i++ ){
                assertNotNull(type.item(i).getTextContent());
                assertNotNull(amount.item(i).getTextContent());
                assertNotNull(to.item(i).getTextContent());
                assertNotNull(from.item(i).getTextContent());
            }



        }catch(Exception e){
            assertTrue(false); //test fails
        }


    }



    @Test
    public void authenticationAPI(){
        // test credentials
        String user = "scotbank";
        String password = "this1password2is3not4secure";

        // Send POST request to fetch OAuth2 token
        HttpResponse<authToken> response = Unirest.post("https://api.asep-strath.co.uk/oauth2/token")
                .field("grant_type", "client_credentials") // defaults to  .header("accept", "application/x-www-form-urlencoded") so no need to include
                .basicAuth(user,password)
                //.header("Authorization", "Bearer "+ new String(encodedBytes).concat("="))
                .asObject(authToken.class);
        authToken auth = response.getBody();
        System.out.println(auth.access_token);
        assertEquals(200, response.getStatus());

    }


    @Test
    public void businessAPI(){
        HttpResponse<JsonNode> help = Unirest.get("https://api.asep-strath.co.uk/api/businesses").asObject(JsonNode.class); // change from JsonNode to business class
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

        assertEquals("logged out", output);
    }

 */

    @Test
    public void logout() {
        App app = new App();
        BankController bankController = app.bankController;
        Context ctx = new MockContext();

        String output = bankController.logout(ctx);
    }

    @Test
    public void checkManager(){
        Context ctx = new MockContext();
        App app = new App();
        BankController bankController = app.bankController;

        boolean check = bankController.checkIfManager(ctx);

        assertEquals(false, bankController.checkIfManager(ctx));
    }





/*
    @Test
    public void AllTransactions(){
        MockContext ctx = new MockContext();
        App app = new App();
        Session currentSession = ctx.session();

        DataSource ds = app.require(DataSource.class);
        Logger log = app.getLog();
        BankController bankController = new BankController(ds,log);


        ModelAndView modelAndView = bankController.viewAllTransactions(currentSession, ctx);
        Map<String, Object> model = new HashMap<>();

        model.put("paidTo", "test");
        model.put("amount", "test");

        assertNotEquals(model,modelAndView.getModel() );


    }



 */



}
