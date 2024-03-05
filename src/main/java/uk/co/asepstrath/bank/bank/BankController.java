package uk.co.asepstrath.bank.bank;
import ch.qos.logback.core.model.Model;
import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import io.jooby.StatusCode;
import io.jooby.annotation.*;
import io.jooby.exception.StatusCodeException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import kong.unirest.core.Unirest;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.example.MyMessage;
import javax.sql.DataSource;
import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.*;
import java.util.Map;
import java.util.Random;

/*
    Example Controller is a Controller from the MVC paradigm.
    The @Path Annotation will tell Jooby what /path this Controller can respond to,
    in this case the controller will respond to requests from <host>/example
 */
@Singleton
@Path("/bank")
public class BankController {

    private final DataSource dataSource;
    private final Logger logger;

    /*
    This constructor can take in any dependencies the controller may need to respond to a request
     */
    public BankController(DataSource ds, Logger log) {
        dataSource = ds;
        logger = log;
    }

    /*
    This is the simplest action a controller can perform
    The @GET annotation denotes that this function should be invoked when a GET HTTP request is sent to <host>/example
    The returned string will then be sent to the requester
     */
    public DataSource getDataSource(){
        return dataSource;
    }
    public Logger getLogger(){
        return logger;
    }
    @GET
    public String welcome() {

        return "Welcome to Jooby!";
    }


    @GET("/homepage")
    public ModelAndView Homepage(){
        return new ModelAndView("Home.hbs");
    }
    /*
    This @Get annotation takes an optional path parameter which denotes the function should be invoked on GET <host>/example/hello
    Note that this function makes it's own request to another API (http://faker.hook.io/) and returns the response
     */


    @GET("/AccountDetails")
    public ModelAndView AccountDetails(){
        return new ModelAndView("AccountDetails.hbs");
    }

    @GET("/Transactions")
    public ModelAndView Transactions(){
        return new ModelAndView("Transactions.hbs");
    }

    @GET("/Summary")
    public ModelAndView Summary(){
        return new ModelAndView("Summary.hbs");
    }

    @GET("/ContactUs")
    public ModelAndView Contact(){
        return new ModelAndView("ContactUs.hbs");
    }

    @GET("/hello")
    public String sayHi() {
        return "Hello";
    }

    @GET("/Login")
    public ModelAndView login(){
        // If no name has been sent within the query URL
        String name = "Please";
        // we must create a model to pass to the "dice" template
        Map<String, Object> model = new HashMap<>();
        model.put("name", name);
        return new ModelAndView("loginView.hbs", model);

    }

    /*public String checkIfLoggedIn(Context ctx){

        Session CurrentSession = ctx.session();
        try {
            return String.valueOf(CurrentSession.get("id"));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }*/

  /*  public void something(ModelAndView model,Context ctx){
        if (checkIfLoggedIn(ctx) != null){
            model.put("UserLoggedIn", Boolean.TRUE);
        } else {
            model.put("UserLoggedIn", Boolean.FALSE);
        }
    } */

    @GET("/Signup")
    public ModelAndView signup(){

        Map<String, Object> model = new HashMap<>();

        return new ModelAndView("signupView.hbs",model);
    }
    @POST
    @Path("/viewAccount")
    public ModelAndView submit(Session session) {
        try(Connection connection = dataSource.getConnection()){

            // Use a prepared statement to avoid SQL injection vulnerabilities
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `accountsTable` WHERE `id` = ?");
            // Set the accountID parameter in the prepared statement
            System.out.println(session.get("id"));
            System.out.println(session.getId());


            statement.setString(1, session.getId());

            Map<String, Object> model = new HashMap<>();
            ResultSet set = statement.executeQuery();
            while(set.next()){
                model.put("accountNum", set.getInt("accountNum"));
                model.put("id", set.getString("id"));
                model.put("name", set.getString("Name"));
                model.put("balance", set.getDouble("Balance"));
                model.put("roundupEnabled", set.getBoolean("roundupEnabled"));

            }

            set.close();
            return new ModelAndView("simpleDetails.hbs", model);

        } catch (SQLException e) {
            // If something does go wrong this will log the stack trace
            logger.error("Database Error Occurred", e);
            // And return a HTTP 500 error to the requester
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Database Error Occurred");
        }
    }

    @GET("/viewAccount")
    public ModelAndView viewAccounts(@QueryParam String accountID){

        if(accountID == null){
            accountID = "1";
        }
        try(Connection connection = dataSource.getConnection()){

            // Use a prepared statement to avoid SQL injection vulnerabilities
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `accountsTable` WHERE `id` = ?");
            // Set the accountID parameter in the prepared statement
            statement.setString(1, accountID);

            Map<String, Object> model = new HashMap<>();
            ResultSet set = statement.executeQuery();
            while(set.next()){
                model.put("name", set.getString("Name"));
                model.put("balance", set.getDouble("balance"));
                model.put("id", set.getInt("id"));

            }
            set.close();

            return new ModelAndView("simpleDetails.hbs", model);

        } catch (SQLException e) {
            // If something does go wrong this will log the stack trace
            logger.error("Database Error Occurred", e);
            // And return a HTTP 500 error to the requester
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Database Error Occurred");
        }
    }


    @GET("/viewAllTransactions")
    public ModelAndView ViewAllTransactions(){
        Map<String, Object> model = new HashMap<>();

        return new ModelAndView("ViewBusinessTransactions.hbs", model);

    }


    /*
    The @POST annotation registers this function as a HTTP POST handler.
    It will look at the body of the POST request and try to deserialise into a MyMessage object
     */
    @POST
    public String post(MyMessage message) {
        return "You successfully POSTed: "+message.Message+ " To: "+message.Recipient;
    }


    @GET("/viewBusinessTransactions")
    public ModelAndView viewAllTransactions() {
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT businessName, withdrawn FROM transactionsTable");

            Map<String, Double> spendingSummary = new HashMap<>();
            while (resultSet.next()) {
                String businessCategory = resultSet.getString("businessName");
                double amountWithdrawn = resultSet.getDouble("withdrawn");

                spendingSummary.put(businessCategory, spendingSummary.getOrDefault(businessCategory, 0.0) + amountWithdrawn);
            }


            Map<String, Object> model = new HashMap<>();
            model.put("spendingSummary", spendingSummary);


            return new ModelAndView("ViewBusinessTransactions.hbs", model);

        } catch (SQLException e) {
            logger.error("Error providing spending data", e);
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Error providing spending data", e);
        }
    }
}
