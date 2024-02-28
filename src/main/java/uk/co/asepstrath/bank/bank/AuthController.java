package uk.co.asepstrath.bank.bank;

import io.jooby.ModelAndView;
import io.jooby.StatusCode;
import io.jooby.annotation.GET;
import io.jooby.annotation.POST;
import io.jooby.annotation.Path;
import io.jooby.annotation.QueryParam;
import io.jooby.exception.StatusCodeException;
import org.slf4j.Logger;
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
    public ModelAndView AuthenticateLogin(String ID, String password) {
        if(ID == null || password == null){
            return bankController.login();
        }
        try(Connection connection = bankController.getDataSource().getConnection()){

            // Use a prepared statement to avoid SQL injection vulnerabilities
            //PreparedStatement statementAccounts = connection.prepareStatement("SELECT * FROM `accountsTable` JOIN `accountsPassword` ON accountsTable.accountNum = accountsPassword.accountNum WHERE `accountNum` = ?");
            PreparedStatement statementAccounts = connection.prepareStatement("SELECT * FROM `accountsTable` WHERE `accountNum` = ?");
            // Set the accountID parameter in the prepared statement
            statementAccounts.setString(1, ID);
            ResultSet setAccounts = statementAccounts.executeQuery();

            //execute query for stored passwords
            PreparedStatement statementPassword = connection.prepareStatement("SELECT * FROM `accountsPassword` WHERE `accountNum` = ?");
            statementPassword.setString(1,ID);
            ResultSet setPassword = statementPassword.executeQuery();
            while(setPassword.next()){
                while(setAccounts.next()) {

                    if (setAccounts.getString("accountNum").equals(setPassword.getString("accountNum")) && setPassword.getString("password").equals(password)  ) {
                        return bankController.submit(ID);
                    }
                    //return bankController.Homepage();
                }
            }

            setAccounts.close();
            setPassword.close();
            return bankController.login();



        } catch (SQLException e) {
            // If something does go wrong this will log the stack trace
            bankController.getLogger().error("Database Error Occurred", e);
            // And return a HTTP 500 error to the requester
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Database Error Occurred");
        }

    }



}
