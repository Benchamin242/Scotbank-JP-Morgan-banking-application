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
import io.jooby.ModelAndView;

import java.util.HashMap;
import java.util.Map;


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
    public ModelAndView Homepage(Context ctx){
        return setBoolean(new ModelAndView("Home.hbs"),ctx);
    }
    /*
    This @Get annotation takes an optional path parameter which denotes the function should be invoked on GET <host>/example/hello
    Note that this function makes it's own request to another API (http://faker.hook.io/) and returns the response
     */


    @GET("/AccountDetails")
    public ModelAndView AccountDetails(Context ctx){

        return setBoolean(new ModelAndView("AccountDetails.hbs"),ctx);
    }

    @GET("/Transactions")
    public ModelAndView Transactions(Context ctx){
        return setBoolean(new ModelAndView("Transactions.hbs"),ctx);
    }

    @GET("/Summary")
    public ModelAndView Summary(Context ctx){
        return setBoolean(new ModelAndView("Summary.hbs"),ctx);
    }

    @GET("/ContactUs")
    public ModelAndView Contact(Context ctx){
        return setBoolean(new ModelAndView("ContactUs.hbs"),ctx);
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

    @GET("/logout")
    public void logout(Context ctx){
        Session CurrentSession = ctx.session();
        CurrentSession.destroy();
        ctx.sendRedirect("/");
    }

    public String checkIfLoggedIn(Context ctx){

        Session CurrentSession = ctx.session();
        try {
            String id = String.valueOf(CurrentSession.get("id"));
            if(id.equals("<missing>")){
                throw new IllegalArgumentException();
            }
            return id;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public ModelAndView setBoolean(ModelAndView model,Context ctx){
        if (checkIfLoggedIn(ctx) != null){
            model.put("userLoggedIn", Boolean.TRUE);
        } else {
            model.put("userLoggedIn", Boolean.FALSE);
        }
        return model;
    }

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
    public ModelAndView viewAccounts(Context ctx){
        Session session= ctx.session();
        //CurrentSession.put("id",resultSet.getString("id"));
        String accountID = session.getId();
        if(accountID == null){
            accountID = "1";
        }
        try(Connection connection = dataSource.getConnection()){

            // Use a prepared statement to avoid SQL injection vulnerabilities
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `accountsTable` WHERE `id` = ?");
            // Set the accountID parameter in the prepared statement
            System.out.println(session.get("id"));
            System.out.println(session.getId());


            statement.setString(1, String.valueOf(session.get("id")));

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
            return setBoolean(new ModelAndView("simpleDetails.hbs", model),ctx);

        } catch (SQLException e) {
            // If something does go wrong this will log the stack trace
            logger.error("Database Error Occurred", e);
            // And return a HTTP 500 error to the requester
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Database Error Occurred");
        }
    }


    /*
    The @POST annotation registers this function as a HTTP POST handler.
    It will look at the body of the POST request and try to deserialise into a MyMessage object
     */
    @POST
    public String post(MyMessage message) {
        return "You successfully POSTed: "+message.Message+ " To: "+message.Recipient;
    }

    @GET("/viewAllTransactions")
    public  ModelAndView viewAllTransactions(Context ctx){
        Session session= ctx.session();

        try(Connection connection = dataSource.getConnection()){

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `transactionHistory` WHERE `id` = ?");
            // Set the accountID parameter in the prepared statement

            Map<String, Object> model = new HashMap<>();
            ResultSet set = statement.executeQuery();
            while(set.next()) {
                model.put("paidTo", set.getString("paidTo"));
                model.put("amount", set.getInt("amount"));
                System.out.println(model.toString());
            }


            return setBoolean(new ModelAndView("ViewAllTransactions.hbs",model),ctx);

        } catch (SQLException e) {
            logger.error("Error providing spending data", e);
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Error providing spending data", e);
        }
    }


    @GET("/viewBusinessTransactions")
    public ModelAndView viewBusinessTransactions(Context ctx) {
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


            return setBoolean(new ModelAndView("ViewBusinessTransactions.hbs"),ctx);


        } catch (SQLException e) {
            logger.error("Error providing spending data", e);
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Error providing spending data", e);
        }
    }


}
