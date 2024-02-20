package uk.co.asepstrath.bank.bank;
import ch.qos.logback.core.model.Model;
import io.jooby.ModelAndView;
import io.jooby.StatusCode;
import io.jooby.annotation.*;
import io.jooby.exception.StatusCodeException;
import kong.unirest.core.Unirest;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.example.MyMessage;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.*;
import java.util.Map;
import java.util.Random;

/*
    Example Controller is a Controller from the MVC paradigm.
    The @Path Annotation will tell Jooby what /path this Controller can respond to,
    in this case the controller will respond to requests from <host>/example
 */
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
    @GET("/hello")
    public String sayHi() {
        return "Hello ";
    }

    @GET("/viewAccount")
    public ModelAndView viewAccounts() {
        Map<String, Object> model = new HashMap<>();
        model.put("nothing", 14);
        return new ModelAndView("simpleDetails.hbs", model);

    }


    @GET("/viewAllTransactions")
    public ModelAndView ViewAllTransactions() {
        Map<String, Object> model = new HashMap<>();

        return new ModelAndView("ViewAllTransactions.hbs", model);

    }


    /*
    The @POST annotation registers this function as a HTTP POST handler.
    It will look at the body of the POST request and try to deserialise into a MyMessage object
     */
    @POST
    public String post(MyMessage message) {
        return "You successfully POSTed: " + message.Message + " To: " + message.Recipient;
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


            return new ModelAndView("ViewAllTransactions.hbs", model);

        } catch (SQLException e) {
            logger.error("Error providing spending data", e);
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Error providing spending data", e);
        }
    }
}
