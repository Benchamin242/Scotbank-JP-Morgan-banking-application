package uk.co.asepstrath.bank.bank;

import io.jooby.ModelAndView;
import io.jooby.StatusCode;
import io.jooby.annotation.GET;
import io.jooby.annotation.POST;
import io.jooby.annotation.Path;
import io.jooby.annotation.QueryParam;
import io.jooby.exception.StatusCodeException;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.App;
import uk.co.asepstrath.bank.example.MyMessage;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/*
    Example Controller is a Controller from the MVC paradigm.
    The @Path Annotation will tell Jooby what /path this Controller can respond to,
    in this case the controller will respond to requests from <host>/example
 */
@Path("/bank/Login")
public class AuthController {

    /*
    This constructor can take in any dependencies the controller may need to respond to a request
     */
    private BankController bankController;

    public AuthController(BankController controller) {
        bankController = controller;
    }

    @POST
    public ModelAndView AuthenticateLogin(String name, String password) {
        if (name == null || password == null) {
            return bankController.login();
        }

        try(Connection connection = bankController.getDataSource().getConnection()){

            // SQL query to simulate FULL OUTER JOIN
            Statement statement = connection.createStatement();;

            String sqlQuery = "SELECT * FROM accountsTable LEFT JOIN accountsPassword ON accountsTable.accountNum = accountsPassword.accountNum " +
                    "UNION " +
                    "SELECT * FROM accountsTable RIGHT JOIN accountsPassword ON accountsTable.accountNum = accountsPassword.accountNum WHERE accountsTable.accountNum IS NULL";
            // Create a Statement
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            while(resultSet.next()){
                if (resultSet.getString("Name").equals(name) && resultSet.getString("password").equals(password)  ) {
                    return bankController.submit(name);
                }
            }
            resultSet.close();
            statement.close();
            connection.close();
            return bankController.login();



        } catch (SQLException e) {
            // If something does go wrong this will log the stack trace
            bankController.getLogger().error("Database Error Occurred", e);
            // And return a HTTP 500 error to the requester
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Database Error Occurred");
        }
    }
}