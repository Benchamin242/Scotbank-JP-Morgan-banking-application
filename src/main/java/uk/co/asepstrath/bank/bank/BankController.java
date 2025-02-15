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
import uk.co.asepstrath.bank.Transactions;
import uk.co.asepstrath.bank.example.MyMessage;
import javax.sql.DataSource;
import javax.xml.transform.Result;
import java.math.BigDecimal;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.*;
import java.util.Map;
import uk.co.asepstrath.bank.Transactions;
import java.util.Random;
import io.jooby.ModelAndView;

import java.util.HashMap;
import java.util.Map;


/*
    Example Controller is a Controller from the MVC paradigm.
    The @Path Annotation will tell Jooby what /path this Controller can respond to,
    in this case the controller will respond to requests from <host>/example
 */
//@Singleton
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
    public ModelAndView Summary(Session session, Context ctx){
        try(Connection connection = dataSource.getConnection()){

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `transactionHistory` WHERE type = 'PAYMENT' AND `from` = ? ORDER BY `to`");

            statement.setString(1, String.valueOf(session.get("id")));

            ResultSet set = statement.executeQuery();

            Map<String, Object> model = new HashMap<>();
            Map<String, Object> spendingSummary = new HashMap<>();
            ArrayList transactions = new ArrayList<Transactions>();

            while(set.next()) {
                Transactions tran = new Transactions(BigDecimal.valueOf(set.getDouble("amount")),set.getString("from"), set.getString("to"),set.getString("type"));

                transactions.add(tran);

            }

            model.put("transactions", transactions);


            return setBoolean(new ModelAndView("Summary.hbs",model),ctx);

        } catch (SQLException e) {
            logger.error("Error providing spending data", e);
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Error providing spending data", e);
        }
    }

    @GET("/ContactUs")
    public ModelAndView Contact(Context ctx){
        return setBoolean(new ModelAndView("ContactUs.hbs"),ctx);
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
    public String logout(Context ctx){
        Session CurrentSession = ctx.session();
        CurrentSession.destroy();
        ctx.sendRedirect("/bank/homepage");
        return "logged out";
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
    public boolean checkIfManager(Context ctx){

        Session CurrentSession = ctx.session();

            String id = String.valueOf(CurrentSession.get("id"));
             if(id.equals("635e583f-0af2-47cb-9625-5b66ba30e188")){
                 return true;
             } else {
                 return false;
             }

        }


    public ModelAndView setBoolean(ModelAndView model,Context ctx){
        if (checkIfLoggedIn(ctx) != null){
            if(checkIfManager(ctx)){
                model.put("ManagerLoggedIn", Boolean.TRUE);
            }
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

            PreparedStatement roundStatement = connection.prepareStatement("SELECT * FROM `roundups` WHERE `id` = ?");
            roundStatement.setString(1, session.getId());
            ResultSet roundSet = roundStatement.executeQuery();

            double roundSubtract = 0;
            while(set.next()){
                model.put("accountNum", set.getInt("accountNum"));
                model.put("id", set.getString("id"));
                model.put("name", set.getString("Name"));
                //model.put("balance", set.getDouble("Balance"));
                roundSubtract = set.getDouble("Balance");
                model.put("postcode", set.getDouble("postcode"));
                System.out.println("postdcode: " + set.getDouble("postcode"));
                model.put("roundupEnabled", set.getBoolean("roundupEnabled"));

            }
            double roundSubtract2 = 0;
            while(roundSet.next()){
                model.put("roundupBalance", roundSet.getDouble("roundupPot"));
                roundSubtract2 = roundSet.getDouble("roundupPot");
            }
            roundSubtract -= roundSubtract2;
            model.put("balance", roundSubtract);
            roundSet.close();
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
            //System.out.println(session.get("id"));
            //System.out.println(session.getId());


            statement.setString(1, String.valueOf(session.get("id")));

            Map<String, Object> model = new HashMap<>();
            ResultSet set = statement.executeQuery();

            PreparedStatement roundStatement = connection.prepareStatement("SELECT * FROM roundups WHERE `id` = ?");
            roundStatement.setString(1, session.getId());
            ResultSet roundSet = roundStatement.executeQuery();

            double roundSubtract = 0;
            while(set.next()){
                model.put("accountNum", set.getInt("accountNum"));
                model.put("id", set.getString("id"));
                model.put("name", set.getString("Name"));
                //model.put("balance", set.getDouble("Balance"));
                roundSubtract = set.getDouble("Balance");
                model.put("postcode", set.getString("postcode"));
                //System.out.println("postcode: " + set.getString("postcode"));
                model.put("roundupEnabled", set.getBoolean("roundupEnabled"));

            }
            double roundSubtract2 = 0;
            while(roundSet.next()){
                model.put("roundupBalance", roundSet.getDouble("roundupPot"));
                roundSubtract2 = roundSet.getDouble("roundupPot");
            }

            roundSubtract -= roundSubtract2;
            model.put("balance", roundSubtract);
            roundSet.close();
            set.close();
            return setBoolean(new ModelAndView("simpleDetails.hbs", model),ctx);

        } catch (SQLException e) {
            // If something does go wrong this will log the stack trace
            logger.error("Database Error Occurred", e);
            // And return a HTTP 500 error to the requester
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Database Error Occurred");
        }
    }

   @GET ("/viewAllAccounts")
    public ModelAndView viewAllAccounts(Session session,Context ctx) { //Session session
        try (Connection connection = dataSource.getConnection()){

            HashMap<String, Object> model = new HashMap<>();

            //grabs the id of whoever is logged on and checks that it is the same id as the manager (in this case our account with id 1)
            String managerID = String.valueOf(session.get("id"));
            System.out.println(managerID);
            if(managerID.equals("635e583f-0af2-47cb-9625-5b66ba30e188")){

                //i want to grab the size of the database, then loop through the entire database pulling out their details
                PreparedStatement sizeStatement = connection.prepareStatement("SELECT COUNT(*) FROM `accountsTable`");
                ResultSet sizeSet = sizeStatement.executeQuery();
                int size = 0;
                if(sizeSet.next()){
                    System.out.println("so far so good");
                    size = sizeSet.getInt(1);
                    System.out.println(String.valueOf(size));
                }

                PreparedStatement pullDetails = connection.prepareStatement("SELECT * FROM `accountsTable`");
                ResultSet set = pullDetails.executeQuery();


                //Map<String, Object> accounts = new HashMap<>();
                List<Map<String, Object>> accounts = new ArrayList<>();

                int counter = 0;
                while (set.next()){
                    Map<String, Object> account = new HashMap<>();
                    //Account temp = new Account(set.getString("Name"), set.getString("id"), set.getBigDecimal("Balance"), set.getBoolean("roundupEnabled"));
                    account.put("Name", set.getString("Name"));
                    account.put("accountNum", set.getInt("accountNum"));
                    account.put("id", set.getString("id"));
                    account.put("Balance", set.getDouble("Balance"));
                    account.put("roundupEnabled", set.getString("roundupEnabled"));
                    account.put("postcode",set.getString("postcode"));
                    accounts.add(account);

                    //accounts.put(set.getInt("accountNum"),temp.toString());

                    counter++;
                }

                Collections.sort(accounts, (account1,account2) -> Double.compare(new Double(account2.get("Balance").toString()), new Double(account1.get("Balance").toString())));
                model.put("accounts",accounts);
                set.close();


                PreparedStatement bigPrepStat = connection.prepareStatement(
                        "SELECT accountsTable.*, SUM(transactionHistory.amount) AS total " +
                                "FROM accountsTable " +
                                "LEFT JOIN transactionHistory ON accountsTable.id = transactionHistory.`from` " +
                                "WHERE transactionHistory.type = 'PAYMENT' " +
                                "GROUP BY accountsTable.id " +
                                "ORDER BY total DESC"
                );
                ResultSet resultBigSet = bigPrepStat.executeQuery();

                List<Map<String, Object>> bigAccounts = new ArrayList<>();

                counter = 0;
                while (resultBigSet.next() && counter < 10){
                    Map<String, Object> account = new HashMap<>();
                    //Account temp = new Account(set.getString("Name"), set.getString("id"), set.getBigDecimal("Balance"), set.getBoolean("roundupEnabled"));
                    account.put("Name", resultBigSet.getString("Name"));
                    account.put("accountNum", resultBigSet.getInt("accountNum"));
                    account.put("id", resultBigSet.getString("id"));
                    account.put("Balance", resultBigSet.getDouble("Balance"));
                    account.put("roundupEnabled", resultBigSet.getString("roundupEnabled"));
                    account.put("postcode",resultBigSet.getString("postcode"));
                    account.put("totalPayments", resultBigSet.getDouble("total"));
                    bigAccounts.add(account);

                    counter++;
                }
                resultBigSet.close();
                model.put("bigSpenders", bigAccounts);

                return setBoolean(new ModelAndView("ViewAllAccounts.hbs", model), ctx);
            }
            else{
                //this is just some testing im doing
                PreparedStatement sizeStatement = connection.prepareStatement("SELECT COUNT(*) FROM `accountsTable`");
                ResultSet sizeSet = sizeStatement.executeQuery();
                int size = 0;
                if(sizeSet.next()){
                    System.out.println("so far so good");
                    size = sizeSet.getInt(1);
                    System.out.println(String.valueOf(size));
                }

                model.put("result", "ERROR: You do not have permission to view this page");
                return setBoolean(new ModelAndView("ViewAllAccounts.hbs", model), ctx);
            }



        } catch (SQLException e) {
            logger.error("Error providing spending data", e);
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Error providing spending data", e);
        }


    }




    /*
    The @POST annotation registers this function as a HTTP POST handler.
    It will look at the body of the POST request and try to deserialise into a MyMessage object
     */


    @GET("/viewAllTransactions")
    public  ModelAndView viewAllTransactions(Session session, Context ctx){

        try(Connection connection = dataSource.getConnection()){

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `transactionHistory` WHERE `from` = ? OR `to` =?");

            statement.setString(1, String.valueOf(session.get("id")));
            statement.setString(2, String.valueOf(session.get("id")));
            //System.out.println("paidFrom: " + String.valueOf(session.get("id")));

            ResultSet set = statement.executeQuery();

            Map<String, Object> model = new HashMap<>();
            Map<String, Object> spendingSummary = new HashMap<>();
            ArrayList transactions = new ArrayList<Transactions>();

            while(set.next()) {
                Date date;
                DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                try {
                    date = df.parse(set.getString("date"));
                }catch(ParseException e){
                    date = null;
                }

                Transactions tran = new Transactions(date, BigDecimal.valueOf(set.getDouble("amount")),set.getString("from"), set.getString("to"),set.getString("type"));

                System.out.println(tran.getTimestamp());
                transactions.add(tran);

            }

            model.put("transactions", transactions);




            return setBoolean(new ModelAndView("ViewAllTransactions.hbs", model), ctx);

        } catch (SQLException e) {
            logger.error("Error providing spending data", e);
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Error providing spending data", e);
        }
    }





}
