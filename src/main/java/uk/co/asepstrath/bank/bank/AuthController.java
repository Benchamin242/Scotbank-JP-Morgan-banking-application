package uk.co.asepstrath.bank.bank;

import io.jooby.ModelAndView;
import io.jooby.Session;
import io.jooby.StatusCode;
import io.jooby.annotation.POST;
import io.jooby.annotation.Path;
import io.jooby.exception.StatusCodeException;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.password.StrongPasswordEncryptor;
import io.jooby.Context;

import java.sql.*;

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
    private StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
    public StrongPasswordEncryptor getPasswordEncryptor(){
        return passwordEncryptor;
    }



    @POST
    public ModelAndView AuthenticateLogin(String name, String password1,Context ctx) {
        if (name == null || password1 == null) {
            return bankController.login();
        }

        try(Connection connection = bankController.getDataSource().getConnection()){

            // SQL query to simulate FULL OUTER JOIN
            Statement statement = connection.createStatement();;

            String sqlQuery = "SELECT * FROM accountsTable LEFT JOIN accountsPassword ON accountsTable.accountNum = accountsPassword.accountNum " +
                    "UNION " +
                    "SELECT * FROM accountsTable RIGHT JOIN accountsPassword ON accountsTable.accountNum = accountsPassword.accountNum WHERE accountsTable.accountNum IS NULL";
            // Create a Statement
            //String encryptedPassword = passwordEncryptor.encryptPassword("test");
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            while(resultSet.next()){
                try { //catches non-encrypted password
                    if (resultSet.getString("Name").equals(name) && resultSet.getString("password") != null && passwordEncryptor.checkPassword(password1, resultSet.getString("password"))) {

                        Session CurrentSession= ctx.session();
                        CurrentSession.put("id",resultSet.getString("id"));
                        //CurrentSession.se(resultSet.getString("id"));
                        //CurrentSession.put("id", resultSet.getString("id"));
                        ctx.sendRedirect("/bank/viewAccount");
                        bankController.viewAllTransactions(CurrentSession);
                        bankController.viewAllAccounts(CurrentSession);
                        return bankController.submit(CurrentSession);
                    }
                }catch(EncryptionOperationNotPossibleException ignored){

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
        /*public String checkIfLoggedIn(Context ctx){

        Session CurrentSession = ctx.session();
        try {
            return String.valueOf(CurrentSession.get("id"));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }*/

  /*  public void setBoolean(ModelAndView model,Context ctx){
        if (checkIfLoggedIn(ctx) != null){
            model.put("UserLoggedIn", Boolean.TRUE);
        } else {
            model.put("UserLoggedIn", Boolean.FALSE);
        }
    } */
    }

}